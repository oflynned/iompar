package com.syzible.iompar;

import android.content.Context;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;

/**
 * Created by ed on 05/12/15.
 */
public class LeapScraping {

    Context context;
    String authKey;
    boolean loggedIn;

    public LeapScraping(Context context) {
        this.context = context;
    }

    public void scrape() throws IOException {
        Toast.makeText(context, "Scraping", Toast.LENGTH_SHORT).show();

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
                    System.out.println(viewState);
                    System.out.println(viewStateKey);

                    response = Jsoup.connect(Globals.LEAP_LOGIN_AUTH_KEY)
                            .cookies(loginCookies)
                            .data("AjaxScriptManager_HiddenField", "")
                            .data("_URLLocalization_Var001", "False")
                            .data("__EVENTTARGET", "")
                            .data("__EVENTARGUMENT", "")
                            .data("__VIEWSTATE", viewStateKey)
                            .data("ContentPlaceHolder1_UserName", Globals.USER_NAME)
                            .data("ContentPlaceHolder1_Password", Globals.USER_PASS)
                            .data("ContentPlaceHolder1_btnlogin", "Login")
                            .method(Connection.Method.POST)
                            .followRedirects(true)
                            .execute();

                    Document document = response.parse();
                    String wallet = document.text();
                    System.out.println(wallet);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        leapThread.run();
    }


    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

}
