package com.syzible.iompar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;


/**
 * Created by ed on 05/12/15.
 */
public class Leap extends WebViewClient {

    Context context;

    public Leap(Context context) {
        this.context = context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void scrape() {
        final WebView webView = new WebView(context);
        webView.loadUrl(Globals.LEAP_LOGIN);
        webView.setVisibility(View.INVISIBLE);

        //add following two lines
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //are we logged in?
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {

                System.out.println("PERFORMING CHECK " + webView.getTitle());

                //check if logged in
                if (webView.getTitle().equals("Sign In To My Account - Leap Card")) {
                    System.out.println("checking if logged in");
                    webView.loadUrl(Globals.LEAP_LOGIN_ACCOUNT_PAGE);
                    //logged out?
                    if (webView.getTitle().equals("Sign In To My Account - Leap Card")) {
                        System.out.println("checking if logged out");
                        //login - works and redirects automatically
                        webView.loadUrl("javascript: {" +
                                "document.getElementById('ContentPlaceHolder1_UserName').value = '" + Globals.USER_NAME + "';" +
                                "document.getElementById('ContentPlaceHolder1_Password').value = '" + Globals.USER_PASS + "';" +
                                "document.getElementById('ContentPlaceHolder1_btnlogin').click();" +
                                "};");

                        //now scrape redirect data on successful login
                        if (webView.getTitle().equals("My Leap Card Overview")) {
                            System.out.println("logged in");
                            setBalance(webView.getUrl());
                        }
                    }
                    System.out.println(webView.getTitle());
                } else if (webView.getTitle().equals("My Leap Card Overview")) {
                    //logged in
                    System.out.println("logged in and in acc overview");
                    webView.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            }
        });

        webView.setVisibility(View.VISIBLE);
        webView.clearCache(true);
        webView.clearHistory();
    }

    public void setBalance(String url){
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            Element masthead = doc.select("div.modFormCol").first();
            System.out.println(masthead.text());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ParseJavaScriptInterface {
        @JavascriptInterface
        public void handleHtml(String html) {
            // Use jsoup on this String here to search for your content.
            Document doc = Jsoup.parse(html);

            // Now you can, for example, retrieve a div with id="username" here
            Element usernameDiv = doc.select("ContentPlaceHolder1_TabContainer2_MyCardsTabPanel_ContentPlaceHolder1_ctrlCardOverViewBODetails_lblTravelCreditBalance").first();
            System.out.println(usernameDiv.text());
        }
    }
}
