package com.syzible.iompar;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ed on 28/10/15.
 */
public class Sync {

    Context context;
    boolean loaded;
    String title, nextDue, arrivalInfo, chosenEndStation, journeyCost, depart, arrive;
    Realtime.LuasDirections lineDirection;

    ArrayList<String> endDestinationList = new ArrayList<>();
    ArrayList<String> waitingTimeList = new ArrayList<>();

    Fares fares = new Fares();

    Fares.FareType fareClass;
    Fares.FarePayment farePayment;

    public Sync(Context context){
        this.context = context;
    }

    public String requestUpdate(Globals.LineDirection direction,
                                String depart,
                                String arrive) throws Exception {
        setLoaded(false);
        threadConnect(direction, depart, arrive, context);
        return getDepartures();
    }

    /**
     * Connects to the RTPI URL with the respective stations given and returns the appropriate
     * scraped values for the depart station to destination
     * @param direction direction in which the user is travelling
     * @param depart    departure station in string format where the user is leaving from
     * @param arrive    name of station at which the user is arriving
     */
    public void threadConnect(final Globals.LineDirection direction, final String depart,
                              final String arrive, final Context context) {
        final Globals globals = new Globals(context);
        Thread downloadThread = new Thread() {
            public void run() {
                setLoaded(false);
                Document doc;
                try {
                    URL url = new URL(Globals.RTPI + globals.getLuasStation(depart));
                    doc = Jsoup.connect(url.toString()).get();

                    System.out.println("got URL");

                    //green line
                    if (direction.equals(Globals.LineDirection.stephens_green_to_brides_glen) ||
                            direction.equals(Globals.LineDirection.stephens_green_to_sandyford)) {
                        if (stationBeforeSandyford(arrive, globals.greenLineBeforeSandyford)) {
                            System.out.println("towards Sandyford/Bride's Glen");
                            scrapeData(doc, "Sandyford", "Bride's Glen", depart, arrive);
                        } else {
                            System.out.println("towards Bride's Glen");
                            scrapeData(doc, "Bride's Glen", depart, arrive);
                        }
                    } else if (direction.equals(Globals.LineDirection.sandyford_to_stephens_green) ||
                            direction.equals(Globals.LineDirection.brides_glen_to_stephens_green)) {
                        System.out.println("towards stephen's green");
                        scrapeData(doc, "St. Stephen's Green", depart, arrive);
                    }

                    //red line - tallaght/point
                    else if (direction.equals(Globals.LineDirection.belgard_to_tallaght) ||
                            direction.equals(Globals.LineDirection.the_point_to_tallaght)) {
                        System.out.println("towards tallaght");
                        scrapeData(doc, "Tallaght", depart, arrive);
                    } else if (direction.equals(Globals.LineDirection.belgard_to_the_point) ||
                            direction.equals(Globals.LineDirection.tallaght_to_the_point) ||
                            direction.equals(Globals.LineDirection.tallaght_to_belgard)) {
                        System.out.println("towards the point");
                        scrapeData(doc, "The Point", depart, arrive);
                    } else if (direction.equals(Globals.LineDirection.the_point_to_belgard)) {
                        System.out.println("towards the point");
                        scrapeData(doc, "Tallaght", depart, arrive);
                    }

                    //saggart/connolly
                    else if (direction.equals(Globals.LineDirection.belgard_to_saggart)) {
                        System.out.println("towards saggart");
                        scrapeData(doc, "Saggart", depart, arrive);
                    } else if (direction.equals(Globals.LineDirection.saggart_to_belgard)) {
                        System.out.println("towards belgard");
                        scrapeData(doc, "The Point", "Belgard", "Connolly", depart, arrive);
                    } else if (direction.equals(Globals.LineDirection.belgard_to_connolly)) {
                        System.out.println("towards the point");
                        scrapeData(doc, "Connolly", depart, arrive);
                    } else if (direction.equals(Globals.LineDirection.connolly_to_belgard)) {
                        System.out.println("towards belgard");
                        scrapeData(doc, "Saggart", depart, arrive);
                    }

                    //connolly/heuston
                    else if (direction.equals(Globals.LineDirection.heuston_to_connolly)) {
                        System.out.println("towards Connolly");
                        scrapeData(doc, "Connolly", "The Point", depart, arrive);
                    } else if (direction.equals(Globals.LineDirection.connolly_to_heuston)) {
                        System.out.println("towards Heuston from town");
                        scrapeData(doc, "Heuston", "Saggart", depart, arrive);
                    }

                    System.out.println(nextDue);
                } catch (IOException e) {
                    setNextDue("Could not connect to realtime services!");
                    setArrivalInfo("Check internet connection!");
                    e.printStackTrace();
                }
                setLoaded(true);
            }
        };
        downloadThread.start();
        setLoaded(false);
    }

    /**
     * scrapes the data from the HTML RTPI website given the start and end stations
     * for the given line.
     * @param doc        document to be scraped
     * @param endStation station the user travels towards
     * @param depart     station the user is coming from
     * @param arrive     station the user is going to
     */
    public void scrapeData(Document doc, String endStation, String depart, String arrive) {
        Elements elements = doc.select("table");
        endDestinationList.clear();
        waitingTimeList.clear();

        Elements tableRowElements = elements.select("tr");
        Element rowCheckEmpty = tableRowElements.get(0);
        Elements rowItemsCheckEmpty = rowCheckEmpty.select("td");

        if (rowItemsCheckEmpty.text().equals("No departures found")) {
            setNextDue("No departures found");
            setArrivalInfo("No times found");
        } else {
            for (int i = 0; i < tableRowElements.size(); i++) {
                Element row = tableRowElements.get(i);
                Elements rowItems = row.select("td");

                for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                    if (rowItems.get(j).text().equals(endStation)) {
                        setChosenEndStation(endStation);
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else {
                        setNextDue("Unavailable, try other line");
                        setArrivalInfo("Unavailable, try other line");
                    }
                }
            }

            if(!endDestinationList.isEmpty()){
                setNextDue(
                        "Origin:" + "\n" + depart + "\n" +
                                "Destination:" + "\n" + arrive);
                setArrivalInfo(
                        "Terminus:" + "\n" + String.valueOf(endDestinationList.get(0)) + "\n" +
                                "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0))) + "\n" +
                                "Cost: €" + fares.getZoneTraversal(convertStringToEnum(
                                getChosenEndStation()), depart, arrive, context, "default"));

                //accessing via dialogs
                setEnumDirection(convertStringToEnum(getChosenEndStation()));
                System.out.println(getEnumDirection());
                setDepart(depart);
                setArrive(arrive);

                setFareClass(fares.getFareType());
                setFarePayment(fares.getFarePayment());
            }
        }
    }

    public void scrapeData(Document doc, String endStation, String endStationAlternate,
                           String depart, String arrive) {
        Elements elements = doc.select("table");
        endDestinationList.clear();
        waitingTimeList.clear();

        Elements tableRowElements = elements.select("tr");
        Element rowCheckEmpty = tableRowElements.get(0);
        Elements rowItemsCheckEmpty = rowCheckEmpty.select("td");

        if (rowItemsCheckEmpty.text().equals("No departures found")) {
            setNextDue("No departures found");
            setArrivalInfo("No times found");
        } else {
            for (int i = 0; i < tableRowElements.size(); i++) {
                Element row = tableRowElements.get(i);
                Elements rowItems = row.select("td");
                for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                    if (rowItems.get(j).text().equals(endStation)) {
                        setChosenEndStation(endStation);
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else if (rowItems.get(j).text().equals(endStationAlternate)) {
                        setChosenEndStation(endStationAlternate);
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else {
                        setNextDue("Unavailable, try other line");
                        setArrivalInfo("Unavailable, try other line");
                    }
                }
            }

            if(!endDestinationList.isEmpty()){
                setNextDue(
                        "Origin:" + "\n" + depart + "\n" +
                                "Destination:" + "\n" + arrive);
                setArrivalInfo(
                        "Terminus:" + "\n" + String.valueOf(endDestinationList.get(0)) + "\n" +
                                "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0))) + "\n" +
                                "Cost: €" + fares.getZoneTraversal(convertStringToEnum(getChosenEndStation()), depart, arrive, context, "default"));

                //accessing via dialogs
                setEnumDirection(convertStringToEnum(getChosenEndStation()));
                System.out.println(getEnumDirection());
                setDepart(depart);
                setArrive(arrive);

                setFareClass(fares.getFareType());
                setFarePayment(fares.getFarePayment());
            }
        }
    }

    public void scrapeData(Document doc, String endStation, String endStationAlternate,
                           String endStationSecondAlternate, String depart, String arrive) {
        Elements elements = doc.select("table");
        endDestinationList.clear();
        waitingTimeList.clear();

        Elements tableRowElements = elements.select("tr");
        Element rowCheckEmpty = tableRowElements.get(0);
        Elements rowItemsCheckEmpty = rowCheckEmpty.select("td");

        if (rowItemsCheckEmpty.text().equals("No departures found")) {
            setNextDue("No departures found");
            setArrivalInfo("No times found");
        } else {
            for (int i = 0; i < tableRowElements.size(); i++) {
                Element row = tableRowElements.get(i);
                Elements rowItems = row.select("td");

                for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                    if (rowItems.get(j).text().equals(endStation)) {
                        setChosenEndStation(endStation);
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else if (rowItems.get(j).text().equals(endStationAlternate)) {
                        setChosenEndStation(endStationAlternate);
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else if (rowItems.get(j).text().equals(endStationSecondAlternate)) {
                        setChosenEndStation(endStationSecondAlternate);
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else {
                        setNextDue("Unavailable, try other line");
                        setArrivalInfo("Unavailable, try other line");
                    }
                }
            }
            if(!endDestinationList.isEmpty()){
                setNextDue(
                        "Origin:" + "\n" + depart + "\n" +
                                "Destination:" + "\n" + arrive);
                setArrivalInfo(
                        "Terminus:" + "\n" + String.valueOf(endDestinationList.get(0)) + "\n" +
                                "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0))) + "\n" +
                                "Cost: €" + fares.getZoneTraversal(convertStringToEnum(getChosenEndStation()), depart, arrive, context, "default"));

                //accessing via dialogs
                setEnumDirection(convertStringToEnum(getChosenEndStation()));
                System.out.println(getChosenEndStation());
                System.out.println(getEnumDirection());
                setDepart(depart);
                setArrive(arrive);

                setFareClass(fares.getFareType());
                setFarePayment(fares.getFarePayment());
            }
        }
    }

    public Realtime.LuasDirections convertStringToEnum(String endStation) {
        if(endStation.equals(context.getResources().getString(R.string.tallaght))){
            return Realtime.LuasDirections.TALLAGHT;
        } else if(endStation.equals(context.getResources().getString(R.string.saggart))){
            return Realtime.LuasDirections.SAGGART;
        } else if(endStation.equals(context.getResources().getString(R.string.connolly))){
            return Realtime.LuasDirections.CONNOLLY;
        } else if(endStation.equals(context.getResources().getString(R.string.the_point))){
            return Realtime.LuasDirections.POINT;
        } else if(endStation.equals(context.getResources().getString(R.string.stephens_green))){
            return Realtime.LuasDirections.STEPHENS_GREEN;
        } else if(endStation.equals(context.getResources().getString(R.string.sandyford))){
            return Realtime.LuasDirections.SANDYFORD;
        } else if(endStation.equals(context.getResources().getString(R.string.brides_glen))){
            return Realtime.LuasDirections.BRIDES_GLEN;
        } else if(endStation.equals(context.getResources().getString(R.string.heuston))){
            return Realtime.LuasDirections.HEUSTON;
        } else {
            return null;
        }
    }

    public String getTimeFormat(String time) {
        switch (time) {
            case "Unavailable":
                return "Unavailable";
            case "Due":
                return "Arriving soon";
            case "1 mins":
                return "1 min away";
            default:
                return time.replaceAll("[^0-9]", "") + " mins away";
        }
    }

    public static boolean stationBeforeSandyford(String arrive, String[] items) {
        for (String item : items) {
            if (arrive.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public String getDepartures() {
        return title;
    }

    public void setNextDue(String nextDue) {
        this.nextDue = nextDue;
    }

    public String getNextDue() {
        return nextDue;
    }

    public void setArrivalInfo(String arrivalInfo) {
        this.arrivalInfo = arrivalInfo;
    }

    public String getArrivalInfo() {
        return arrivalInfo;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setChosenEndStation(String chosenEndStation) {
        this.chosenEndStation = chosenEndStation;
    }

    public String getChosenEndStation() {
        return chosenEndStation;
    }

    public void setFareClass(Fares.FareType fareType){this.fareClass = fareType;}
    public Fares.FareType getFareClass(){return fareClass;}
    public void setFarePayment(Fares.FarePayment farePayment){this.farePayment = farePayment;}
    public Fares.FarePayment getFarePayment(){return farePayment;}
    public void setEnumDirection(Realtime.LuasDirections lineDirection){this.lineDirection=lineDirection;}
    public Realtime.LuasDirections getEnumDirection(){return lineDirection;}
    public void setDepart(String depart){this.depart=depart;}
    public String getDepart(){return depart;}
    public void setArrive(String arrive){this.arrive=arrive;}
    public String getArrive(){return arrive;}
}
