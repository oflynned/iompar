package com.syzible.iompar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ed on 28/10/15.
 */
public class Sync {

    boolean loaded;
    String title, endStation, nextDue;

    ArrayList<String> endDestinationList = new ArrayList<>();
    ArrayList<String> waitingTimeList = new ArrayList<>();

    Globals globals = new Globals();

    //luas
    public String requestUpdate(Globals.LineDirection direction,
                                String depart,
                                String arrive) throws Exception {

        threadConnect(direction, depart, arrive);
        return getDepartures();
    }

    /**
     Connects to the RTPI URL with the respective stations given and returns the appropriate
     scraped values for the depart station to destination
     @param direction   direction in which the user is travelling
     @param depart      departure station in string format where the user is leaving from
     @param arrive      name of station at which the user is arriving
     */
    public void threadConnect(final Globals.LineDirection direction, final String depart, final String arrive) {
        Thread downloadThread = new Thread() {
            public void run() {
                setLoaded(false);
                Document doc;
                try {
                    URL url = new URL(globals.RTPI + globals.getLuasStation(depart));
                    doc = Jsoup.connect(url.toString()).get();

                    System.out.println("got URL");


                    if(direction.equals(Globals.LineDirection.stephens_green_to_brides_glen) ||
                            direction.equals(Globals.LineDirection.stephens_green_to_sandyford)) {
                        if (stationBeforeSandyford(depart, arrive, globals.greenLineBeforeSandyford)) {
                            // green line - travelling to any station common to bride's glen/sandyford
                            // from stephen's green
                            System.out.println("towards Sandyford/Bride's Glen");
                            scrapeData(doc, "Sandyford", "Bride's Glen", depart, arrive);

                        } else {
                            // else we're travelling to beyond sandyford and can only take
                            // bride's glen trams away from stephen's green
                            System.out.println("towards Bride's Glen");
                            scrapeData(doc, "Bride's Glen", depart, arrive);
                        }
                    } else if(direction.equals(Globals.LineDirection.sandyford_to_stephens_green) ||
                            direction.equals(Globals.LineDirection.brides_glen_to_stephens_green)){
                        // else if we're travelling inversely from sandyford/bride's glen towards
                        // stephen's green, we can take any tram as they all have the same terminus
                        System.out.println("towards stephen's green");
                        scrapeData(doc, "St. Stephen's Green", depart, arrive);
                    } else if (direction.equals(Globals.LineDirection.the_point_to_tallaght)){
                        // else we're dealing with the red line of the luas and must check direction
                        // and poll the appropriate sub-lines
                        // getting scaldy in tallaght? red line from point to the square
                        System.out.println("towards tallaght");
                        scrapeData(doc, "Tallaght", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.tallaght_to_the_point)){
                        // else we're moving from tallaght towards the point on a tram
                        // and probably spitting at people on the way
                        System.out.println("towards the point from tallaght");
                        scrapeData(doc, "The Point", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.saggart_to_the_point)){
                        // else heading from Saggart towards town where I need to interchange at Belgard
                        // ONLY TRUE FOR SAGGART-TOWN ROUTE OUTSIDE OF PEAK TIMES
                        // weekdays: before 7am, after 6:30pm
                        // saturday: before 8am, after 6pm
                        // sunday: all day
                        System.out.println("towards the point from saggart WITH CERTAIN RULES");
                        scrapeData(doc, "Belgard", "The Point", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.the_point_to_saggart)) {
                        // else we're going from town to saggart
                        // this is mostly running, need to check if it polled as exceptions were thrown
                        System.out.println("towards Saggart from town");
                        scrapeData(doc, "Saggart", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.heuston_to_connolly)) {
                        System.out.println("towards Connolly");
                        scrapeData(doc, "Connolly", depart, arrive);
                    } else if(direction.equals(Globals.LineDirection.connolly_to_heuston)){
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
                    setNextDue("Could not connect to realtime services - check internet connection");
                    e.printStackTrace();
                }
                setLoaded(true);
            }
        };
        downloadThread.start();
        setLoaded(false);
    }

    public void scrapeData(Document doc, String endStation, String depart, String arrive){
        Elements elements = doc.select("table");
        endDestinationList.clear();
        waitingTimeList.clear();
        Elements tableRowElements = elements.select("tr");
        if(tableRowElements != null) {
            for (int i = 0; i < tableRowElements.size(); i++) {
                Element row = tableRowElements.get(i);
                Elements rowItems = row.select("td");
                for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                    if (rowItems.get(j).text().equals(endStation)) {
                        endDestinationList.add(rowItems.get(j).text());
                        waitingTimeList.add(rowItems.get(j + 1).text());
                        System.out.println(rowItems.get(j).text());
                        System.out.println(rowItems.get(j + 1).text());
                    } else if (rowItems.get(j).equals("No departures found")){
                        endDestinationList.add("Unavailable");
                        waitingTimeList.add("Unavailable");
                    }
                }
            }
        }
        setNextDue(
                "The next Luas terminates in " + String.valueOf(endDestinationList.get(0)) + "\n" +
                        "Departing from: " + depart + "\n" +
                        "Destination: " + arrive + "\n" +
                        "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0))));
    }

    public void scrapeData(Document doc, String endStation, String endStationAlternate, String depart, String arrive){
        Elements elements = doc.select("table");
        endDestinationList.clear();
        waitingTimeList.clear();
        Elements tableRowElements = elements.select("tr");
        if(tableRowElements != null) {
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
                    } else if (rowItems.get(j).equals("No departures found")){
                        endDestinationList.add("Unavailable");
                        waitingTimeList.add("Unavailable");
                    }
                }
            }
        }
        setNextDue(
                "The next Luas terminates in " + String.valueOf(endDestinationList.get(0)) + "\n" +
                        "Departing from: " + depart + "\n" +
                        "Destination: " + arrive + "\n" +
                        "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0))));
    }

    /**
     Connects to Leap Card login service via ASPX and returns the balance within the page source
     recursively through an independent asynchronous thread
     @note THROWING EXCEPTION ON LOGIN
     */
    public void leapConnect(){
        Thread leapThread = new Thread() {
            public void run() {
                try {
                    Connection.Response response = Jsoup.connect(Globals.LEAP_LOGIN)
                            .method(Connection.Method.GET)
                            .timeout(Globals.TEN_SECONDS)
                            .execute();

                    Document responseDocument = response.parse();
                    Map<String, String> loginCookies = response.cookies();

                    Element viewState = responseDocument.select("input[name=__VIEWSTATE]").first();
                    String viewStateKey = viewState.attr("value");

                    System.out.println("2nd response");

                    response = Jsoup.connect(Globals.LEAP_LOGIN)
                            .timeout(Globals.TEN_SECONDS)
                            .cookies(loginCookies)
                            .validateTLSCertificates(true)
                            .userAgent("Mozilla/5.0")
                            .data(loginCookies)
                            .data("AjaxScriptManager_HiddenField", "")
                            .data("_URLLocalization_Var001", "False")
                            .data("__EVENTTARGET", "")
                            .data("__EVENTARGUMENT", "")
                            .data("__VIEWSTATE", viewStateKey)
                            .data("ctl00$ContentPlaceHolder1$UserName", Globals.USER_NAME)
                            .data("ctl00$ContentPlaceHolder1$Password", Globals.USER_PASS)
                            .method(Connection.Method.POST)
                            .followRedirects(true)
                            .execute();

                    System.out.println("Received response");

                    Document document = response.parse();
                    System.out.println(document);
                    String wallet = document.text();
                    System.out.println(wallet);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        leapThread.run();
    }

    public String getTimeFormat(String time){
        switch (time) {
            case "Due":
                return "Arriving soon.";
            case "1 mins":
                return "1 min away.";
            default:
                return time.replaceAll("[^0-9]", "") + " mins away.";
        }
    }

    public static boolean stationBeforeSandyford(String depart, String arrive, String[] items)
    {
        for (String item : items) {
            if (arrive.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public void setEndStation(String endStation){
        this.endStation = endStation;
    }

    public String getEndStation(){
        return endStation;
    }

    public void setDepartures(String title) {
        this.title = title;
    }

    public String getDepartures() {
        return title;
    }

    public void setNextDue(String nextDue){this.nextDue = nextDue;}
    public String getNextDue(){return nextDue;}

    public void setLoaded(boolean loaded){this.loaded = loaded;}
    public boolean isLoaded(){return loaded;}
}
