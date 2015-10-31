package com.syzible.iompar;

import android.app.Activity;
import android.content.Context;

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

    String title, endStation;

    ArrayList<String> endDestinationList = new ArrayList<>();
    ArrayList<String> waitingTimeList = new ArrayList<>();

    Globals globals = new Globals();

    public Sync(Context context) {
        this.context = context;
    }

    //generic
    public String requestUpdate(Globals.Type type) throws Exception {
        return "";
    }

    //bus
    public String requestUpdate(Globals.Type type, int stopNumber) {
        return "";
    }

    //luas
    public String requestUpdate(Globals.Type type,
                                Globals.LineDirection direction,
                                String station) throws Exception {
        switch (type) {
            case luas:
                threadConnect(direction, station);
                break;
            default:
                break;
        }
        return getDepartures();
    }

    public void threadConnect(final Globals.LineDirection direction, final String station) {
        Thread downloadThread = new Thread() {
            public void run() {
                Document doc;
                String nextDue = "";
                try {
                    URL url = new URL(globals.RTPI + globals.getLuasStation(station));
                    doc = Jsoup.connect(url.toString()).get();

                    System.out.println("got URL");

                    if(direction.equals(Globals.LineDirection.stephens_green_to_brides_glen) ||
                            direction.equals(Globals.LineDirection.stephens_green_to_sandyford)) {
                        if (stationBeforeSandyford(station, globals.greenLineBeforeSandyford)) {

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
                            nextDue = "The next Luas terminating in " +
                                    String.valueOf(endDestinationList.get(0)) +
                                    " departing from " + station +
                                    " is " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                        } else {
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

                            nextDue = "The next Luas terminating in " +
                                    String.valueOf(endDestinationList.get(0)) +
                                    " departing from " + station +
                                    " is " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                        }
                    } else if(direction.equals(Globals.LineDirection.sandyford_to_stephens_green) ||
                            direction.equals(Globals.LineDirection.brides_glen_to_stephens_green)){
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

                        nextDue = "The next Luas terminating in " +
                                String.valueOf(endDestinationList.get(0)) +
                                " departing from " + station +
                                " is " + getTimeFormat(String.valueOf(waitingTimeList.get(0)));
                    }

                    System.out.println(nextDue);
                    setDepartures(nextDue);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        downloadThread.start();
    }

    public String getTimeFormat(String time){
        if(time.equals("Due")){
            return "arriving soon.";
        } else if(time.equals("1 mins")) {
            return "1 min away";
        } else {
            return time.replaceAll("[^0-9]","") + " mins away.";
        }
    }

    public static boolean stationBeforeSandyford(String inputString, String[] items)
    {
        for(int i =0; i < items.length; i++)
        {
            if(inputString.contains(items[i]))
            {
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
}
