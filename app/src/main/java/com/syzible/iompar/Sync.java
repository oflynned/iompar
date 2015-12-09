package com.syzible.iompar;

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
    boolean loaded;
    String title, endStation, nextDue, arrivalInfo;

    ArrayList<String> endDestinationList = new ArrayList<>();
    ArrayList<String> waitingTimeList = new ArrayList<>();

    Globals globals = new Globals();
    Fares fares = new Fares();

    //luas
    public String requestUpdate(Globals.LineDirection direction,
                                String depart,
                                String arrive) throws Exception {

        threadConnect(direction, depart, arrive);
        return getDepartures();
    }

    /**
     * Connects to the RTPI URL with the respective stations given and returns the appropriate
     * scraped values for the depart station to destination
     *
     * @param direction direction in which the user is travelling
     * @param depart    departure station in string format where the user is leaving from
     * @param arrive    name of station at which the user is arriving
     */
    public void threadConnect(final Globals.LineDirection direction, final String depart, final String arrive) {
        Thread downloadThread = new Thread() {
            public void run() {
                setLoaded(false);
                Document doc;
                try {
                    URL url = new URL(Globals.RTPI + globals.getLuasStation(depart));
                    doc = Jsoup.connect(url.toString()).get();

                    System.out.println("got URL");

                    /**
                     * @note crash on same station selection for start and end stops
                     */
                    if (direction.equals(Globals.LineDirection.stephens_green_to_brides_glen) ||
                            direction.equals(Globals.LineDirection.stephens_green_to_sandyford)) {
                        if (stationBeforeSandyford(depart, arrive, globals.greenLineBeforeSandyford)) {
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
                    } else if(direction.equals(Globals.LineDirection.belgard_to_saggart)){
                        System.out.println("towards saggart");
                        scrapeData(doc, "Saggart", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.connolly_to_saggart)){
                        System.out.println("towards saggart");
                        scrapeData(doc, "Saggart", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.saggart_to_connolly)){
                        System.out.println("towards saggart");
                        scrapeData(doc, "Connolly", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.belgard_to_tallaght)){
                        System.out.println("towards tallaght");
                        scrapeData(doc, "Tallaght", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.belgard_to_the_point) ||
                                direction.equals(Globals.LineDirection.tallaght_to_belgard) ||
                                direction.equals(Globals.LineDirection.saggart_to_belgard)){
                        System.out.println("towards the point");
                        scrapeData(doc, "The Point", "Belgard", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.the_point_to_belgard)){
                        System.out.println("towards the point");
                        scrapeData(doc, "Tallaght", "Saggart", depart, arrive);
                    } else if (direction.equals(Globals.LineDirection.heuston_to_connolly)) {
                        System.out.println("towards Connolly");
                        scrapeData(doc, "Connolly", depart, arrive);
                    } else if (direction.equals(Globals.LineDirection.connolly_to_heuston)) {
                        System.out.println("towards Heuston from town");
                        scrapeData(doc, "Heuston", depart, arrive);
                    } else {
                        setNextDue("Jumped to else loop -- no conditions satisfied -- check luas directions");
                        System.out.println("Jumped to else loop -- no conditions satisfied -- check luas directions");
                        setLoaded(true);
                        return;
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
     *
     * @param doc        document to be scraped
     * @param endStation station the user travels towards
     * @param depart     station the user is coming from
     * @param arrive     station the user is going to
     * @crash Belgard catastrophe where Saggart line changes depending on given time
     */
    public void scrapeData(Document doc, String endStation, String depart, String arrive) {
        Elements elements = doc.select("table");
        endDestinationList.clear();
        waitingTimeList.clear();
        Elements tableRowElements = elements.select("tr");
        if (tableRowElements != null) {
            for (int i = 0; i < tableRowElements.size(); i++) {
                Element row = tableRowElements.get(i);
                Elements rowItems = row.select("td");
                for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                    if (rowItems.get(j).text().equals(endStation)) {
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    }
                }
            }
        }
        //must poll time & payment method or show both
        fares.setFareType(Fares.FareType.ADULT);
        fares.setFareCaps(Fares.FareCaps.ON_PEAK);
        fares.setFarePayment(Fares.FarePayment.LEAP);
        fares.setFareJourney(Fares.FareJourney.SINGLE);
        fares.setLuasFareCost(Fares.LuasFareCost.THREE_ZONES);
        fares.calculateFare();

        setNextDue(
                "Origin:" + "\n" + depart + "\n" +
                        "Destination:" + "\n" + arrive);
        setArrivalInfo(
                "Terminus:" + "\n" + String.valueOf(endDestinationList.get(0)) + "\n" +
                        "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0))) + "\n" +
                        "Cost: €" + fares.getFare());
    }

    public void scrapeData(Document doc, String endStation, String endStationAlternate,
                           String depart, String arrive) {
        Elements elements = doc.select("table");
        endDestinationList.clear();
        waitingTimeList.clear();
        Elements tableRowElements = elements.select("tr");
        if (tableRowElements != null) {
            for (int i = 0; i < tableRowElements.size(); i++) {
                Element row = tableRowElements.get(i);
                Elements rowItems = row.select("td");
                for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                    if (rowItems.get(j).text().equals(endStation)) {
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else if (rowItems.get(j).text().equals(endStationAlternate)) {
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    }
                }
            }
        }
        //must poll time & payment method or show both
        fares.setFareType(Fares.FareType.ADULT);
        fares.setFareCaps(Fares.FareCaps.ON_PEAK);
        fares.setFarePayment(Fares.FarePayment.LEAP);
        fares.setFareJourney(Fares.FareJourney.SINGLE);
        fares.setLuasFareCost(Fares.LuasFareCost.THREE_ZONES);
        fares.calculateFare();

        /**
         * @crash when no trams available
         */
        setNextDue(
                "Origin:" + "\n" + depart + "\n" +
                        "Destination:" + "\n" + arrive);
        setArrivalInfo(
                "Terminus:" + "\n" + String.valueOf(endDestinationList.get(0)) + "\n" +
                        "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0))) + "\n" +
                        "Cost: €" + fares.getFare());
    }

    public void scrapeData(Document doc, String endStation, String endStationAlternate,
                           String endStationSecondAlternate, String depart, String arrive) {
        Elements elements = doc.select("table");
        endDestinationList.clear();
        waitingTimeList.clear();
        Elements tableRowElements = elements.select("tr");
        if (tableRowElements != null) {
            for (int i = 0; i < tableRowElements.size(); i++) {
                Element row = tableRowElements.get(i);
                Elements rowItems = row.select("td");
                for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                    if (rowItems.get(j).text().equals(endStation)) {
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else if (rowItems.get(j).text().equals(endStationAlternate)) {
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else if (rowItems.get(j).text().equals(endStationSecondAlternate)) {
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    }
                }
            }
        }
        //must poll time & payment method or show both
        fares.setFareType(Fares.FareType.ADULT);
        fares.setFareCaps(Fares.FareCaps.ON_PEAK);
        fares.setFarePayment(Fares.FarePayment.LEAP);
        fares.setFareJourney(Fares.FareJourney.SINGLE);
        fares.setLuasFareCost(Fares.LuasFareCost.THREE_ZONES);
        fares.calculateFare();

        /**
         * @crash when no trams available
         */
        setNextDue(
                "Origin:" + "\n" + depart + "\n" +
                        "Destination:" + "\n" + arrive);
        setArrivalInfo(
                "Terminus:" + "\n" + String.valueOf(endDestinationList.get(0)) + "\n" +
                        "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0))) + "\n" +
                        "Cost: €" + fares.getFare());
    }

    public String getTimeFormat(String time) {
        switch (time) {
            case "Due":
                return "Arriving soon";
            case "1 mins":
                return "1 min away";
            default:
                return time.replaceAll("[^0-9]", "") + " mins away";
        }
    }

    public static boolean stationBeforeSandyford(String depart, String arrive, String[] items) {
        for (String item : items) {
            if (arrive.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setDepartures(String title) {
        this.title = title;
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
}
