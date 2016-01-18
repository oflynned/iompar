package com.glassbyte.iompar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;


/**
 * Created by ed on 05/12/15.
 */
public class Leap extends WebViewClient {

    Context context;
    String balance;
    boolean synced;
    int incorrectDetails;

    public Leap(Context context) {
        this.context = context;
    }

    /**
     * launch a webview to auto login via a headless browser native to Android
     * in order to retrieve session cookies, parse to JSoup, and scrape
     * page acquired in order to retrieve the balance
     *
     * @note JavaScript interface on URL manipulation
     * @note does not account for >1 Leap card on the account
     */
    @JavascriptInterface
    @SuppressLint("SetJavaScriptEnabled")
    public void scrape() {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            final String username = cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_USER_NAME);
            final String password = cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_PASSWORD);
            databaseHelper.close();
            sqLiteDatabase.close();
            cursor.close();

            //retrieve details of currently active leap card
            setSynced(false);
            final WebView webView = new WebView(context);
            webView.loadUrl(Globals.LEAP_LOGIN);

            Toast.makeText(context, R.string.connecting, Toast.LENGTH_LONG).show();

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);

            //are we logged in?
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {

                    System.out.println("PERFORMING CHECK " + webView.getTitle());
                    Toast.makeText(context, R.string.retrieving_currently_active_leap, Toast.LENGTH_LONG).show();

                    //check if logged in
                    if (webView.getTitle().equals("Sign In To My Account - Leap Card")) {
                        System.out.println("checking if logged in");
                        webView.loadUrl("about:blank");
                        webView.loadUrl(Globals.LEAP_LOGIN_ACCOUNT_PAGE);
                        //logged out?
                        if (webView.getTitle().equals("Sign In To My Account - Leap Card")) {
                            System.out.println("checking if logged out");
                            //login - works and redirects automatically
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);

                                    System.out.println(username + " " + password);

                                    webView.loadUrl("javascript: {" +
                                            "document.getElementById('ContentPlaceHolder1_UserName').value = '" + username + "';" +
                                            "document.getElementById('ContentPlaceHolder1_Password').value = '" + password + "';" +
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
                                                    .timeout(Globals.TEN_SECONDS)
                                                    .post();

                                            String returnedPost = document.text();
                                            //trim to (€)
                                            returnedPost = returnedPost.replaceAll(".*(€)", "");
                                            returnedPost = returnedPost.replaceAll("\\)\\s", "");
                                            //trim after and format
                                            returnedPost = returnedPost.replaceAll("\\u00a0\\u00a0\\u00a0\\u00a0\\u00a0\\u00a0\\u00a0.*", "");
                                            returnedPost = returnedPost.replaceAll("[^\\d.-]", "");
                                            returnedPost = Fares.formatDecimals(returnedPost);

                                            //if it's negative, format accordingly
                                            if (returnedPost.contains("-")) {
                                                returnedPost = returnedPost.replaceAll("\\-", "");
                                                returnedPost = "-€" + returnedPost;
                                            } else {
                                                returnedPost = "€" + returnedPost;
                                            }

                                            System.out.println("Reported Leap balance:");
                                            System.out.println(returnedPost);

                                            setBalance(returnedPost);
                                            setSynced(true);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        System.out.println("REPORTED TITLE " + webView.getTitle());
                                        System.out.println("not logged in");
                                        incorrectDetails++;
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
                                returnedPost = Fares.formatDecimals(returnedPost);

                                //if it's negative, format accordingly
                                if (returnedPost.contains("-")) {
                                    returnedPost = returnedPost.replaceAll("\\-", "");
                                    returnedPost = "-€" + returnedPost;
                                } else {
                                    returnedPost = "€" + returnedPost;
                                }

                                System.out.println("Reported Leap balance:");
                                System.out.println(returnedPost);

                                setBalance(returnedPost);
                                setSynced(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            webView.clearCache(true);
            webView.clearHistory();
        } else {
            Toast.makeText(context, R.string.no_active_leap_add_enable, Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    @SuppressLint("SetJavaScriptEnabled")
    public void retrieveLeapcardrBalance(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            final String username = cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_USER_NAME);
            final String password = cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_PASSWORD);
            databaseHelper.close();
            sqLiteDatabase.close();
            cursor.close();

            Toast.makeText(context, R.string.connecting, Toast.LENGTH_LONG).show();

            final WebView webView = new WebView(context);
            webView.loadUrl("http://www.leapcardr.com");

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            setSynced(false);
            Toast.makeText(context, R.string.retrieving_currently_active_leap, Toast.LENGTH_LONG).show();

            //are we logged in?
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    webView.loadUrl("javascript: {" +
                            "document.getElementById('username').value = '" + username + "';" +
                            "document.getElementById('password').value = '" + password + "';" +
                            "document.forms[0].submit();" +
                            "};");

                    CookieSyncManager.getInstance().sync();
                    String session = CookieManager.getInstance().getCookie(url).replaceAll("session=", "");
                    System.out.println(session);

                    Document document;
                    try {
                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        String leapCard = MainActivity.getActiveLeapNumber(databaseHelper).substring(0, 10);
                        databaseHelper.close();

                        document = Jsoup.connect("http://www.leapcardr.com/login")
                                .cookie("session", session)
                                .timeout(Globals.SIXTY_SECONDS)
                                .get();

                        String balance;
                        String returnedPost = document.text();
                        String[] data = returnedPost.split("\\s");
                        for (int i = 0; i < data.length; i++) {
                            if (data[i].contains(leapCard)) {
                                balance = data[i + 3];

                                if (balance.contains("-")) {
                                    balance = balance.replaceAll("\\-", "").replaceAll("€", "");
                                    balance = "-€" + balance;
                                } else {
                                    balance = "€" + balance.replaceAll("€", "");
                                }
                                System.out.println("Reported balance: " + balance);
                                setBalance(balance);
                                setSynced(true);
                                break;
                            }
                        }
                        webView.clearCache(true);
                        webView.clearHistory();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(context, R.string.no_active_leap_add_enable, Toast.LENGTH_LONG).show();
        }
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
    public String getBalance() {
        return balance;
    }
    public boolean isSynced(){ return synced; }
    public void setSynced(boolean synced){ this.synced = synced; }
    public int getIncorrectDetails(){ return incorrectDetails; }
}
