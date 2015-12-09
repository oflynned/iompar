package com.syzible.iompar;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;


/**
 * Created by ed on 05/12/15.
 */
public class LeapScraping extends WebViewClient{

    Context context;

    public LeapScraping(Context context) {
        this.context = context;
    }

    public void scrape(){
        WebView webView = new WebView(context);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setJavaScriptEnabled(true);

        /*HashMap<String, String> map = new HashMap<>();
        String token = loadTokenFromPreference(this);
        String sessionCookie = "staging=" + token;
        map.put("Cookie", sessionCookie);
        webView.loadUrl(Globals.LEAP_LOGIN, map);*/
    }
}
