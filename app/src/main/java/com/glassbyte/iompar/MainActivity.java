package com.glassbyte.iompar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String ON_BACK_PRESSED_EVENT = "on_back_pressed_event";

    //fragment classes
    Realtime realtime = new Realtime();
    Fares fares = new Fares();
    ManageLeapCards manageLeapCards = new ManageLeapCards();
    Expenditures expenditures = new Expenditures();
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    Globals globals;

    InterstitialAd interstitialAd;

    TextView barName, barLeapCardNumber, currentState;
    DrawerLayout drawer;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        interstitialAd = new InterstitialAd(getBaseContext());
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        //set appropriate language
        globals = new Globals(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        globals.setIrish(sharedPreferences.getBoolean(getResources()
                .getString(R.string.pref_key_irish), false), getResources());

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //fixes problems associated with bug in tools v23 not allowing text overriding
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        barName = (TextView) headerView.findViewById(R.id.bar_name);
        barLeapCardNumber = (TextView) headerView.findViewById(R.id.bar_leapcard_number);
        currentState = (TextView) headerView.findViewById(R.id.current_state);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setFragment();

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();
        interstitialAd.show();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        interstitialAd.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent broadcastIntent = new Intent(ON_BACK_PRESSED_EVENT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        setNavigationBarProfile();
    }

    public void setNavigationBarProfile(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        String leapNumber;

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            leapNumber = cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER);
        } else {
            leapNumber = getString(R.string.using_cash);
        }

        databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);

        barName.invalidate();
        barName.setText(sharedPreferences.getString(getString(R.string.pref_key_name), ""));
        barLeapCardNumber.invalidate();
        barLeapCardNumber.setText(leapNumber);

        //set appropriate state if leap is positive, if cash is being used or anything else
        currentState.invalidate();
        currentState.setText(getString(R.string.positive_leapcard_drawer));

        sqLiteDatabase.close();
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
        } else if(id == R.id.action_about){
            startActivity(new Intent(this, AboutUs.class));
        } else if(id == R.id.visit_us){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.glassbyte.com/")));
        } else if(id == R.id.intro){
            startActivity(new Intent(this, IntroActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_realtime) {
            changeFragment(this.realtime);
        } else if (id == R.id.nav_fare_calculator) {
            changeFragment(this.fares);
        } else if (id == R.id.nav_manage_leap_cards) {
            changeFragment(this.manageLeapCards);
        } else if (id == R.id.nav_expenditures) {
            changeFragment(this.expenditures);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, this.realtime)
                .addToBackStack(null)
                .commit();
    }

    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}

