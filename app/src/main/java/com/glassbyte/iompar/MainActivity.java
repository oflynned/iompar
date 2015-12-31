package com.glassbyte.iompar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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

    //helper classes
    Realtime realtime = new Realtime();
    Fares fares = new Fares();
    ManageLeapCards manageLeapCards = new ManageLeapCards();
    Expenditures expenditures = new Expenditures();
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    Globals globals;

    InterstitialAd interstitialAd;

    TextView barName, barLeapCardNumber, currentState;
    public static DrawerLayout drawer;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    String fragName;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //ayyy lmao our income
        //AsynchronousInterstitial asynchronousInterstitial = new AsynchronousInterstitial();
        //asynchronousInterstitial.execute();

        SQLiteDatabase sqliteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        if(cursor.getCount() > 0) {
            AsynchronousLeapChecking asynchronousLeapChecking = new AsynchronousLeapChecking(this);
            asynchronousLeapChecking.execute();
        }
        cursor.close();
        sqliteDatabase.close();

        setFragment();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getSelected().equals("Realtime")){
                Intent broadcastIntent = new Intent(ON_BACK_PRESSED_EVENT);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        setNavigationBarProfile();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        globals.setIrish(sharedPreferences.getBoolean(getResources()
                .getString(R.string.pref_key_irish), false), getResources());

        setNavigationBarProfile();
    }

    private void setNavigationBarProfile(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        String leapNumber;

        if(cursor.getCount() > 0){
            String balance = sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance),
                    getString(R.string.unsynced));
            cursor.moveToFirst();
            leapNumber = cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER) + " (" + balance + ")";
            if(balance.contains("-€")){
                currentState.setText(getString(R.string.negative_leapcard_drawer));
            } else if (!balance.contains("€") || balance.contains(getString(R.string.unsynced))){
                currentState.setText(R.string.unsynced);
            } else {
                currentState.setText(getString(R.string.positive_leapcard_drawer));
            }
        } else {
            leapNumber = getString(R.string.cash_leapcard_drawer);
            currentState.setText(R.string.cannot_sync_leap_balance);
        }

        databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);

        barName.invalidate();
        barName.setText(sharedPreferences.getString(getString(R.string.pref_key_name), ""));
        barLeapCardNumber.invalidate();
        barLeapCardNumber.setText(leapNumber);

        //set appropriate state if leap is positive, if cash is being used or anything else
        currentState.invalidate();

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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_realtime) {
            setSelected("Realtime");
            changeFragment(this.realtime);
        } else if (id == R.id.nav_fare_calculator) {
            setSelected("Fares");
            changeFragment(this.fares);
        } else if (id == R.id.nav_manage_leap_cards) {
            setSelected("Manage");
            changeFragment(this.manageLeapCards);
        } else if (id == R.id.nav_expenditures) {
            setSelected("Expenditures");
            changeFragment(this.expenditures);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment() {
        setSelected("Realtime");
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

    public void setSelected(String fragName){this.fragName=fragName;}
    public String getSelected(){return fragName;}

    class AsynchronousInterstitial extends AsyncTask<Void, Void, Void> {

        AdRequest adRequest;

        @Override
        protected Void doInBackground(Void... params) {
            interstitialAd = new InterstitialAd(getBaseContext());
            interstitialAd.setAdUnitId(Globals.ADMOB_ID_DEVELOPMENT);
            adRequest = new AdRequest.Builder().build();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            interstitialAd.loadAd(adRequest);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            });
        }
    }

    public class AsynchronousLeapChecking extends AsyncTask<Void, Void, Void> {

        Leap leap;
        Context context;

        public AsynchronousLeapChecking(Context context){this.context=context;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            leap = new Leap(context)  ;
            leap.scrape();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(!leap.isSynced()){
                System.out.println("NOT synced and sleeping for 1s");
                try {
                    Thread.sleep(Globals.ONE_SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(leap.isSynced()){
                System.out.println("is synced");
                if (leap.isSynced() && !(sharedPreferences.getBoolean(getString(R.string.pref_key_first_sync), false))) {
                    new AlertDialog.Builder(context)
                            .setTitle("First Time Sync")
                            .setMessage("This is your first time syncing this Leap card. Your online account shows that this Leap card has a balance of " + leap.getBalance() + ", is this correct?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Reported balance " + leap.getBalance(), Toast.LENGTH_LONG).show();
                                    editor = sharedPreferences.edit();
                                    editor.putBoolean(getString(R.string.pref_key_first_sync), true);
                                    editor.apply();

                                    //STORE BALANCE IN TABLE
                                    editor.putString(getString(R.string.pref_key_last_synced_balance), leap.getBalance().replace("€", ""));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //amend this value as it's not correct
                                    final CorrectBalanceDialog correctBalanceDialog = new CorrectBalanceDialog();
                                    correctBalanceDialog.show(MainActivity.this.getFragmentManager(), "AmendBalance");
                                    correctBalanceDialog.setSetBalanceDialogListener(new CorrectBalanceDialog.SetBalanceListener() {
                                        @Override
                                        public void onDoneClick(android.app.DialogFragment dialog) {
                                            if (correctBalanceDialog.getBalance().contains("-")) {
                                                Toast.makeText(getApplicationContext(), "Amended balance -€" + correctBalanceDialog.getBalance().replace("-", ""), Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Amended balance €" + correctBalanceDialog.getBalance(), Toast.LENGTH_LONG).show();
                                            }

                                            editor = sharedPreferences.edit();
                                            editor.putBoolean(getString(R.string.pref_key_first_sync), true);
                                            editor.apply();

                                            //STORE BALANCE IN TABLE
                                            editor.putString(getString(R.string.pref_key_last_synced_balance), correctBalanceDialog.getBalance());
                                        }
                                    });
                                }
                            })
                            .show();
                }
            }
        }
    }
}

