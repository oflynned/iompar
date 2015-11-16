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
                Document doc;
                try {
                    setLoaded(false);
                    URL url = new URL(globals.RTPI + globals.getLuasStation(depart));
                    doc = Jsoup.connect(url.toString()).get();

                    System.out.println("got URL");

                    // green line - travelling to any station common to bride's glen/sandyford
                    // from stephen's green
                    if(direction.equals(Globals.LineDirection.stephens_green_to_brides_glen) ||
                            direction.equals(Globals.LineDirection.stephens_green_to_sandyford)) {
                        if (stationBeforeSandyford(depart, arrive, globals.greenLineBeforeSandyford)) {

                            System.out.println("towards Sandyford/Bride's Glen");

                            Elements elements = doc.select("table");

                            endDestinationList.clear();
                            waitingTimeList.clear();

                            //print out elements within rows
                            Elements tableRowElements = elements.select("tr");
                            for (int i = 0; i < tableRowElements.size(); i++) {
                                Element row = tableRowElements.get(i);
                                Elements rowItems = row.select("td");
                                for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                                    endDestinationList.add(rowItems.get(j).text());
                                    waitingTimeList.add(rowItems.get(j + 1).text());
                                    System.out.println(rowItems.get(j).text());
                                    System.out.println(rowItems.get(j + 1).text());
                                }
                            }
                            nextDue =
                                    "The next Luas terminates in " + String.valueOf(endDestinationList.get(0)) + "\n" +
                                            "Departing from: " + depart + "\n" +
                                            "Destination: " + arrive + "\n" +
                                            "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                        } else {
                            // else we're travelling to beyond sandyford and can only take
                            // bride's glen trams away from stephen's green
                            System.out.println("towards Bride's Glen");

                            Elements elements = doc.select("table");

                            endDestinationList.clear();
                            waitingTimeList.clear();

                            //print out elements within rows
                            Elements tableRowElements = elements.select("tr");
                            for (int i = 0; i < tableRowElements.size(); i++) {
                                Element row = tableRowElements.get(i);
                                Elements rowItems = row.select("td");
                                for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                                    if (rowItems.get(j).text().equals("Bride's Glen")) {
                                        endDestinationList.add(rowItems.get(j).text());
                                        waitingTimeList.add(rowItems.get(j + 1).text());
                                        System.out.println(rowItems.get(j).text());
                                        System.out.println(rowItems.get(j + 1).text());
                                    }
                                }
                            }

                            nextDue =
                                    "The next Luas terminates in " + String.valueOf(endDestinationList.get(0)) + "\n" +
                                            "Departing from: " + depart + "\n" +
                                            "Destination: " + arrive + "\n" +
                                            "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                        }
                    } else if(direction.equals(Globals.LineDirection.sandyford_to_stephens_green) ||
                            direction.equals(Globals.LineDirection.brides_glen_to_stephens_green)){
                        // else if we're travelling inversely from sandyford/bride's glen towards
                        // stephen's green, we can take any tram as they all have the same terminus
                        Elements elements = doc.select("table");

                        endDestinationList.clear();
                        waitingTimeList.clear();

                        System.out.println("towards stephen's green");

                        //print out elements within rows
                        Elements tableRowElements = elements.select("tr");
                        for (int i = 0; i < tableRowElements.size(); i++) {
                            Element row = tableRowElements.get(i);
                            Elements rowItems = row.select("td");
                            for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                                if (rowItems.get(j).text().equals("St. Stephen's Green")) {
                                    endDestinationList.add(rowItems.get(j).text());
                                    waitingTimeList.add(rowItems.get(j + 1).text());
                                    System.out.println(rowItems.get(j).text());
                                    System.out.println(rowItems.get(j + 1).text());
                                }
                            }
                        }
                        nextDue =
                                "The next Luas terminates in " + String.valueOf(endDestinationList.get(0)) + "\n" +
                                "Departing from: " + depart + "\n" +
                                "Destination: " + arrive + "\n" +
                                "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                    } else if (direction.equals(Globals.LineDirection.the_point_to_tallaght)){
                        Elements elements = doc.select("table");

                        endDestinationList.clear();
                        waitingTimeList.clear();

                        System.out.println("towards tallaght");

                        //print out elements within rows
                        Elements tableRowElements = elements.select("tr");
                        for (int i = 0; i < tableRowElements.size(); i++) {
                            Element row = tableRowElements.get(i);
                            Elements rowItems = row.select("td");
                            for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                                if (rowItems.get(j).text().equals("Tallaght")) {
                                    endDestinationList.add(rowItems.get(j).text());
                                    waitingTimeList.add(rowItems.get(j + 1).text());
                                    System.out.println(rowItems.get(j).text());
                                    System.out.println(rowItems.get(j + 1).text());
                                }
                            }
                        }
                        nextDue =
                                "The next Luas terminates in " + String.valueOf(endDestinationList.get(0)) + "\n" +
                                        "Departing from: " + depart + "\n" +
                                        "Destination: " + arrive + "\n" +
                                        "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                    } else if(direction.equals(Globals.LineDirection.tallaght_to_the_point)){
                        Elements elements = doc.select("table");

                        endDestinationList.clear();
                        waitingTimeList.clear();

                        System.out.println("towards the point");

                        //print out elements within rows
                        Elements tableRowElements = elements.select("tr");
                        for (int i = 0; i < tableRowElements.size(); i++) {
                            Element row = tableRowElements.get(i);
                            Elements rowItems = row.select("td");
                            for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                                if (rowItems.get(j).text().equals("The Point")) {
                                    endDestinationList.add(rowItems.get(j).text());
                                    waitingTimeList.add(rowItems.get(j + 1).text());
                                    System.out.println(rowItems.get(j).text());
                                    System.out.println(rowItems.get(j + 1).text());
                                }
                            }
                        }
                        nextDue =
                                "The next Luas terminates in " + String.valueOf(endDestinationList.get(0)) + "\n" +
                                        "Departing from: " + depart + "\n" +
                                        "Destination: " + arrive + "\n" +
                                        "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                    } else if(direction.equals(Globals.LineDirection.saggart_to_the_point)){
                        Elements elements = doc.select("table");

                        endDestinationList.clear();
                        waitingTimeList.clear();

                        System.out.println("towards the point");

                        //print out elements within rows
                        Elements tableRowElements = elements.select("tr");
                        for (int i = 0; i < tableRowElements.size(); i++) {
                            Element row = tableRowElements.get(i);
                            Elements rowItems = row.select("td");
                            for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                                if (rowItems.get(j).text().equals("Belgard")) {
                                    endDestinationList.add(rowItems.get(j).text());
                                    waitingTimeList.add(rowItems.get(j + 1).text());
                                    System.out.println(rowItems.get(j).text());
                                    System.out.println(rowItems.get(j + 1).text());
                                }
                            }
                        }
                        nextDue =
                                "The next Luas terminates in " + String.valueOf(endDestinationList.get(0)) + "\n" +
                                        "Departing from: " + depart + "\n" +
                                        "Destination: " + arrive + "\n" +
                                        "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                    } else if(direction.equals(Globals.LineDirection.the_point_to_saggart)) {
                        Elements elements = doc.select("table");

                        endDestinationList.clear();
                        waitingTimeList.clear();

                        System.out.println("towards Saggart");

                        //print out elements within rows
                        Elements tableRowElements = elements.select("tr");
                        for (int i = 0; i < tableRowElements.size(); i++) {
                            Element row = tableRowElements.get(i);
                            Elements rowItems = row.select("td");
                            for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                                if (rowItems.get(j).text().equals("Saggart")) {
                                    endDestinationList.add(rowItems.get(j).text());
                                    waitingTimeList.add(rowItems.get(j + 1).text());
                                    System.out.println(rowItems.get(j).text());
                                    System.out.println(rowItems.get(j + 1).text());
                                }
                            }
                        }
                        nextDue =
                                "The next Luas terminates in " + String.valueOf(endDestinationList.get(0)) + "\n" +
                                        "Departing from: " + depart + "\n" +
                                        "Destination: " + arrive + "\n" +
                                        "ETA: " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                    } else {
                        System.out.println("Jumped to else loop -- check luas directions");
                    }
                    System.out.println(nextDue);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("4");
                setNextDue(nextDue);
                setLoaded(true);
            }
        };
        downloadThread.start();
        System.out.println("1");
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
                            .timeout(10 * 1000)
                            .execute();

                    Document responseDocument = response.parse();
                    Map<String, String> loginCookies = response.cookies();

                    Element viewState = responseDocument.select("input[name=__VIEWSTATE]").first();
                    String viewStateKey = viewState.attr("value");

                    System.out.println("2nd response");

                    response = Jsoup.connect(Globals.LEAP_LOGIN)
                            .timeout(10 * 1000)
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
