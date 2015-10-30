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
import java.util.concurrent.TimeUnit;

/**
 * Created by ed on 28/10/15.
 */
public class Sync {
    Context context;
    Activity activity;

    String title, endStation;

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

                    if(stationBeforeSandyford(station, globals.greenLineBeforeSandyford)){

                        System.out.println("before sandyford");

                        Elements elements = doc.select("table");

                        ArrayList<String> endDestinationList = new ArrayList<>();
                        ArrayList<String> waitingTimeList = new ArrayList<>();

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
                                " is " + String.valueOf(waitingTimeList.get(0)) +
                                " mins away!";
                    } else {
                        System.out.println("before Bride's Glen");

                        Elements elements = doc.select("table");

                        ArrayList<String> endDestinationList = new ArrayList<>();
                        ArrayList<String> waitingTimeList = new ArrayList<>();

                        //print out elements within rows
                        Elements tableRowElements = elements.select("tr");
                        for (int i = 0; i < tableRowElements.size(); i++) {
                            Element row = tableRowElements.get(i);
                            Elements rowItems = row.select("td");
                            for (int j = 1; j < rowItems.size() - 1; j = j + 2) {
                                if(rowItems.get(j).text().equals("Bride's Glen")) {
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
                                " is " + String.valueOf(waitingTimeList.get(0)) +
                                " away!";
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
