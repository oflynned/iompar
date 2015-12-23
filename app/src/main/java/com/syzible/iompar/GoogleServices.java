package com.syzible.iompar;

import android.content.Context;
import android.content.ContextWrapper;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by ed on 23/12/15.
 */
public class GoogleServices extends ContextWrapper {

    InterstitialAd interstitialAd;

    public GoogleServices(Context base) {
        super(base);
    }

    public void showInterstitial(){
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestInterstitial();
            }
        });
        requestInterstitial();
    }

    private void requestInterstitial(){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
        interstitialAd.loadAd(adRequest);
    }
}
