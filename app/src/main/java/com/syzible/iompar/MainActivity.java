package com.syzible.iompar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String ON_BACK_PRESSED_EVENT = "on_back_pressed_event";

    //fragment classes
    Realtime realtime = new Realtime();
    Fares fares = new Fares();
    ManageLeapCards manageLeapCards = new ManageLeapCards();
    Expenditures expenditures = new Expenditures();
    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    TextView barName, barLeapCardNumber;
    DrawerLayout drawer;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setFragment();

        AsynchronousFareRetrieval asynchronousFareRetrieval = new AsynchronousFareRetrieval();
        asynchronousFareRetrieval.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent broadcastIntent = new Intent(ON_BACK_PRESSED_EVENT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        }
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

    public void setFares(){
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
        cursor.moveToFirst();
        int count = cursor.getCount();

        //single fares table
        if(count > 0){
            try {
                URL url = new URL(Globals.LUAS_FARES);
                Document doc = Jsoup.connect(url.toString()).get();

                Elements elements = doc.select("table");
                Elements tableRowElements = elements.select("tr");
                String[] splitFaresCheck = new String[7];

                //check if first row has changed
                for (int j = 2; j < 3; j++) {
                    String fare = tableRowElements.get(j).text();
                    splitFaresCheck = fare.split("€");
                }

                //check if fares have changed from last sync
                if (!cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT)
                        .equals(String.valueOf(Double.parseDouble(splitFaresCheck[1])))) {
                    //obliterate and repopulate
                    databaseHelper.clearTable(Database.LuasSingleFares.TABLE_NAME);
                    databaseHelper.clearTable(Database.LuasReturnFares.TABLE_NAME);
                    databaseHelper.clearTable(Database.LeapCaps.TABLE_NAME);

                    //single ticket
                    for (int j = 2; j <= 6; j++) {
                        String fare = tableRowElements.get(j).text();
                        String[] splitFares = fare.split("€");

                        databaseHelper.insertFares(Database.LuasSingleFares.TABLE_NAME,
                                fares.formatDecimals(splitFares[1]),
                                fares.formatDecimals(splitFares[2]),
                                fares.formatDecimals(splitFares[3]),
                                fares.formatDecimals(splitFares[4]),
                                fares.formatDecimals(splitFares[5]),
                                fares.formatDecimals(splitFares[6]),
                                null, null, null, null, null, null);
                    }

                    //return ticket
                    for (int j = 8; j <= 12; j++) {
                        String fare = tableRowElements.get(j).text();
                        String[] splitFares = fare.split("€");

                        databaseHelper.insertFares(Database.LuasReturnFares.TABLE_NAME,
                                null, null, null, null, null, null,
                                fares.formatDecimals(splitFares[1]),
                                fares.formatDecimals(splitFares[2]),
                                null, null, null, null);
                    }

                    //caps
                    for (int j = 15; j <= 17; j++) {
                        String fare = tableRowElements.get(j).text();
                        String[] splitFares = fare.split("€");

                        databaseHelper.insertFares(Database.LeapCaps.TABLE_NAME,
                                null, null, null, null, null, null, null, null,
                                fares.formatDecimals(splitFares[1]),
                                fares.formatDecimals(splitFares[2]),
                                fares.formatDecimals(splitFares[3]),
                                fares.formatDecimals(splitFares[4]));
                    }

                    databaseHelper.printTableContents(Database.LuasSingleFares.TABLE_NAME);
                    databaseHelper.printTableContents(Database.LuasReturnFares.TABLE_NAME);
                    databaseHelper.printTableContents(Database.LeapCaps.TABLE_NAME);
                } else {
                    databaseHelper.printTableContents(Database.LuasSingleFares.TABLE_NAME);
                    databaseHelper.printTableContents(Database.LuasReturnFares.TABLE_NAME);
                    databaseHelper.printTableContents(Database.LeapCaps.TABLE_NAME);
                    System.out.println("Fares same as online");
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            //clear as a safety measure
            databaseHelper.clearTable(Database.LuasSingleFares.TABLE_NAME);
            databaseHelper.clearTable(Database.LuasReturnFares.TABLE_NAME);
            databaseHelper.clearTable(Database.LeapCaps.TABLE_NAME);

            //not populated, proceed to populate table
            URL url;
            Document doc;
            try {
                url = new URL(Globals.LUAS_FARES);
                doc = Jsoup.connect(url.toString()).get();

                Elements elements = doc.select("table");
                Elements tableRowElements = elements.select("tr");

                //single ticket
                for (int j = 2; j <= 6; j++) {
                    String fare = tableRowElements.get(j).text();
                    String[] splitFares = fare.split("€");

                    databaseHelper.insertFares(Database.LuasSingleFares.TABLE_NAME,
                            fares.formatDecimals(splitFares[1]),
                            fares.formatDecimals(splitFares[2]),
                            fares.formatDecimals(splitFares[3]),
                            fares.formatDecimals(splitFares[4]),
                            fares.formatDecimals(splitFares[5]),
                            fares.formatDecimals(splitFares[6]),
                            null, null, null, null, null, null);

                    System.out.println("Single Zone " + (j-1) + ": "
                                    + fares.formatDecimals(splitFares[1]) + ", "
                                    + fares.formatDecimals(splitFares[2]) + ", "
                                    + fares.formatDecimals(splitFares[3]) + ", "
                                    + fares.formatDecimals(splitFares[4]) + ", "
                                    + fares.formatDecimals(splitFares[5]) + ", "
                                    + fares.formatDecimals(splitFares[6])
                    );
                    System.out.println("------------------------------------");
                }

                //return ticket
                for (int j = 8; j <= 12; j++) {
                    String fare = tableRowElements.get(j).text();
                    String[] splitFares = fare.split("€");

                    databaseHelper.insertFares(Database.LuasReturnFares.TABLE_NAME,
                            null, null, null, null, null, null,
                            fares.formatDecimals(splitFares[1]),
                            fares.formatDecimals(splitFares[2]),
                            null, null, null, null);

                    System.out.println("Return Zone " + (j-7) + ": "
                            + fares.formatDecimals(splitFares[1]) + ", "
                            + fares.formatDecimals(splitFares[2]));
                    System.out.println("------------------------------------");
                }

                //caps
                for (int j = 15; j <= 17; j++) {
                    String fare = tableRowElements.get(j).text();
                    String[] splitFares = fare.split("€");

                    databaseHelper.insertFares(Database.LeapCaps.TABLE_NAME,
                            null, null, null, null, null, null, null, null,
                            fares.formatDecimals(splitFares[1]),
                            fares.formatDecimals(splitFares[2]),
                            fares.formatDecimals(splitFares[3]),
                            fares.formatDecimals(splitFares[4]));

                            System.out.println("Caps type #" + (j - 14) + ": "
                                    + fares.formatDecimals(splitFares[1]) + ", "
                                    + fares.formatDecimals(splitFares[2]) + ", "
                                    + fares.formatDecimals(splitFares[3]) + ", "
                                    + fares.formatDecimals(splitFares[4]));
                    System.out.println("------------------------------------");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        databaseHelper.printTableContents(Database.LuasSingleFares.TABLE_NAME);
        databaseHelper.printTableContents(Database.LuasReturnFares.TABLE_NAME);
        databaseHelper.printTableContents(Database.LeapCaps.TABLE_NAME);

        cursor.close();
        sqLiteDatabase.close();
    }

    class AsynchronousFareRetrieval extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            setFares();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Fares synced", Toast.LENGTH_SHORT).show();
        }
    }
}

