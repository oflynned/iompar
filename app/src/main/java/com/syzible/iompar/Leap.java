package com.syzible.iompar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;


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
        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //are we logged in?
        webView.setWebViewClient(new WebViewClient() {
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
                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                webView.loadUrl("javascript: {" +
                                        "document.getElementById('ContentPlaceHolder1_UserName').value = '" + Globals.USER_NAME + "';" +
                                        "document.getElementById('ContentPlaceHolder1_Password').value = '" + Globals.USER_PASS + "';" +
                                        "document.getElementById('ContentPlaceHolder1_btnlogin').click();" +
                                        "};");

                                //now scrape redirect data on successful login
                                if (webView.getTitle().equals("My Leap Card Overview")) {
                                    System.out.println("logged in");
                                    //retrieve balance from html source
                                    Document doc;
                                    try {
                                        doc = Jsoup.connect(Globals.LEAP_LOGIN_ACCOUNT_PAGE).get();
                                        Elements elements = doc.getAllElements();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    System.out.println("not logged in");
                                }
                            }
                        });
                    }
                } else if (webView.getTitle().equals("My Leap Card Overview")) {
                    //logged in
                    System.out.println("logged in and in acc overview");
                }
            }
        });

        webView.setVisibility(View.VISIBLE);
        webView.clearCache(true);
        webView.clearHistory();
    }
}
