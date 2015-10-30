package com.syzible.iompar;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.android.AndroidDriver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ed on 28/10/15.
 */
public class Sync {
    Context context;
    Activity activity;

    Globals globals = new Globals();

    public Sync(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
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

        StringBuilder result = new StringBuilder();

        switch(type){
            case luas:
                //retrieve the data from the RTPI server for station
                URL url = new URL(globals.RTPI + globals.getLuasStation(station));
                URL localHost = new URL("http://localhost");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                try {
                    Document document = Jsoup.connect("http://www.syzible.com/").get();
                    String title = document.title();
                    Toast.makeText(context, "Title: " + title, Toast.LENGTH_SHORT).show();
                    Elements elements = document.select("table");
                } catch (IOException exception){
                    exception.printStackTrace();
                }

                break;
            default:
                break;
        }
        return result.toString();
    }
}
