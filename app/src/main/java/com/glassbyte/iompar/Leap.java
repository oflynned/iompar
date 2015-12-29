package com.glassbyte.iompar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
        final Fares fares = new Fares();

        //add following two lines
        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //are we logged in?
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                System.out.println("PERFORMING CHECK " + webView.getTitle());
                Toast.makeText(context, "Retrieving currently active Leap balance...", Toast.LENGTH_LONG).show();

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

                                    //kk, let's get the cookies to retain the session
                                    CookieSyncManager.getInstance().sync();
                                    String cookies = CookieManager.getInstance().getCookie(url);

                                    //split cookies with ; and = regexes
                                    String[] cookiesGenerated = cookies.replaceAll("\\s", "").split("(;)|(=)");
                                    for (int i = 0; i < cookiesGenerated.length; i++) {
                                        System.out.println("cookie index #" + i + ": " + cookiesGenerated[i]);
                                    }
                                    System.out.println("Reported URL " + url);

                                    //take cookies and connect with session via jsoup
                                    try {
                                        Document document = Jsoup.connect(url)
                                                .cookie(cookiesGenerated[0], cookiesGenerated[1])
                                                .cookie(cookiesGenerated[2], cookiesGenerated[3])
                                                .cookie(cookiesGenerated[4], cookiesGenerated[5])
                                                .cookie(cookiesGenerated[6], cookiesGenerated[7])
                                                .cookie(cookiesGenerated[8], cookiesGenerated[9])
                                                .cookie(cookiesGenerated[10], cookiesGenerated[11])
                                                .cookie(cookiesGenerated[13], cookiesGenerated[14])
                                                .timeout(Globals.TEN_SECONDS)
                                                .post();

                                        String returnedPost = document.text();
                                        //trim to (€)
                                        returnedPost = returnedPost.replaceAll(".*(€)", "");
                                        returnedPost = returnedPost.replaceAll("\\)\\s", "");
                                        //trim after and format
                                        returnedPost = returnedPost.replaceAll("\\u00a0\\u00a0\\u00a0\\u00a0\\u00a0\\u00a0\\u00a0.*", "");
                                        returnedPost = returnedPost.replaceAll("[^\\d.-]", "");
                                        returnedPost = fares.formatDecimals(returnedPost);

                                        //if it's negative, format accordingly
                                        if (returnedPost.contains("-")) {
                                            returnedPost = returnedPost.replaceAll("\\-", "");
                                            returnedPost = "-€" + returnedPost;
                                        } else {
                                            returnedPost = "€" + returnedPost;
                                        }

                                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                                        databaseHelper.clearTable(Database.LeapLogin.TABLE_NAME);
                                        databaseHelper.insertRecord(
                                                Database.LeapLogin.TABLE_NAME,
                                                null, null, null, null, null, null, 0, returnedPost, 0, 0, 0, false,
                                                Globals.USER_LEAP_NUMBER, Globals.USER_NAME, Globals.USER_EMAIL, Globals.USER_PASS, true);

                                        System.out.println("Reported Leap balance:");
                                        System.out.println(returnedPost);
                                        Toast.makeText(context, returnedPost, Toast.LENGTH_LONG).show();
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

                    if (webView.getTitle().equals("My Leap Card Overview")) {
                        System.out.println("logged in");

                        //kk, let's get the cookies to retain the session
                        CookieSyncManager.getInstance().sync();
                        String cookies = CookieManager.getInstance().getCookie(url);

                        //split cookies with ; and = regexes
                        String[] cookiesGenerated = cookies.replaceAll("\\s", "").split("(;)|(=)");
                        for (int i = 0; i < cookiesGenerated.length; i++) {
                            System.out.println("cookie index #" + i + ": " + cookiesGenerated[i]);
                        }
                        System.out.println("Reported URL " + url);

                        //take cookies and connect with session via jsoup
                        try {
                            Document document = Jsoup.connect(url)
                                    .cookie(cookiesGenerated[0], cookiesGenerated[1])
                                    .cookie(cookiesGenerated[2], cookiesGenerated[3])
                                    .cookie(cookiesGenerated[4], cookiesGenerated[5])
                                    .cookie(cookiesGenerated[6], cookiesGenerated[7])
                                    .cookie(cookiesGenerated[8], cookiesGenerated[9])
                                    .cookie(cookiesGenerated[10], cookiesGenerated[11])
                                    .cookie(cookiesGenerated[13], cookiesGenerated[14])
                                    .timeout(Globals.TEN_SECONDS)
                                    .post();

                            String returnedPost = document.text();
                            //trim to (€)
                            returnedPost = returnedPost.replaceAll(".*(€)", "");
                            returnedPost = returnedPost.replaceAll("\\)\\s", "");
                            //trim after and format
                            returnedPost = returnedPost.replaceAll("\\u00a0\\u00a0\\u00a0\\u00a0\\u00a0\\u00a0\\u00a0.*", "");
                            returnedPost = returnedPost.replaceAll("[^\\d.-]", "");
                            returnedPost = fares.formatDecimals(returnedPost);

                            //if it's negative, format accordingly
                            if (returnedPost.contains("-")) {
                                returnedPost = returnedPost.replaceAll("\\-", "");
                                returnedPost = "-€" + returnedPost;
                            } else {
                                returnedPost = "€" + returnedPost;
                            }

                            DatabaseHelper databaseHelper = new DatabaseHelper(context);
                            databaseHelper.clearTable(Database.LeapLogin.TABLE_NAME);
                            databaseHelper.insertRecord(
                                    Database.LeapLogin.TABLE_NAME,
                                    null, null, null, null, null, null, 0, returnedPost, 0, 0, 0, false,
                                    Globals.USER_LEAP_NUMBER, Globals.USER_NAME, Globals.USER_EMAIL, Globals.USER_PASS, true);

                            System.out.println("Reported Leap balance:");
                            System.out.println(returnedPost);
                            Toast.makeText(context, returnedPost, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        webView.setVisibility(View.VISIBLE);
        webView.clearCache(true);
        webView.clearHistory();
        }
    }
