package com.syzible.iompar;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String ON_BACK_PRESSED_EVENT = "on_back_pressed_event";

    //fragment classes
    Realtime realtime = new Realtime();
    Fares fares = new Fares();
    Remind remind = new Remind();
    Around around = new Around();
    ManageLeapCards manageLeapCards = new ManageLeapCards();
    Balance balance = new Balance();
    Expenditures expenditures = new Expenditures();
    Globals globals = new Globals();
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

        /* @bug barName and barLeapCardNumber are null and cannot have text instantiated?
        barName = (TextView) findViewById(R.id.bar_name);
        barLeapCardNumber = (TextView) findViewById(R.id.bar_leapcard_number);

        System.out.println(barName.getText().toString());
        System.out.println(barLeapCardNumber.getText().toString());

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LEAP_LOGIN, null);
        cursor.moveToFirst();
        System.out.println(cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_EMAIL));
        barName.setText(cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_EMAIL));
        barLeapCardNumber.setText(cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER));
        cursor.close();
        sqLiteDatabase.close();*/
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
            return true;
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
        } else if (id == R.id.nav_remind_me) {
            changeFragment(this.remind);
        } else if (id == R.id.nav_around_me) {
            changeFragment(this.around);
        } else if (id == R.id.nav_manage_leap_cards) {
            changeFragment(this.manageLeapCards);
        } else if (id == R.id.nav_check_balance) {
            changeFragment(this.balance);
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
