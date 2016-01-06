package com.glassbyte.iompar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

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

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);

        if (sharedPreferences.getBoolean(getString(R.string.pref_key_leap_sync), false)) {
            if (cursor.getCount() > 0) {
                AsynchronousLeapChecking asynchronousLeapChecking = new AsynchronousLeapChecking();
                asynchronousLeapChecking.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
        cursor.close();
        sqLiteDatabase.close();

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
        AsynchronousInterstitial asynchronousInterstitial = new AsynchronousInterstitial();
        asynchronousInterstitial.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (!sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("") &&
                !sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), "").equals("") &&
                !sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("unsynced") &&
                !sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), "").equals("unsynced")) {
            getCurrentBalance(databaseHelper, sharedPreferences, this);
        }

        setFragment();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSelected().equals("Realtime")) {
                Intent broadcastIntent = new Intent(ON_BACK_PRESSED_EVENT);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavigationBarProfile();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        globals.setIrish(sharedPreferences.getBoolean(getResources()
                .getString(R.string.pref_key_irish), false), getResources());

        if (!sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("") &&
                !sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), "").equals("") &&
                !sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("unsynced") &&
                !sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), "").equals("unsynced")) {
            getCurrentBalance(databaseHelper, sharedPreferences, this);
        }
        setNavigationBarProfile();
    }

    private void setNavigationBarProfile() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        String leapNumber;

        System.out.println(sharedPreferences.getString(getString(R.string.pref_key_current_balance),""));
        System.out.println(sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance),""));

        if (cursor.getCount() > 0) {
            String balance;
            if (sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("")) {
                balance = sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance),
                        getString(R.string.unsynced));
            } else {
                balance = sharedPreferences.getString(getString(R.string.pref_key_current_balance),
                        getString(R.string.unsynced));
            }
            cursor.moveToFirst();
            leapNumber = cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER) + " (" + balance + ")";
            if (balance.contains("-€")) {
                currentState.setText(getString(R.string.negative_leapcard_drawer));
            } else if (!balance.contains("€") || balance.contains(getString(R.string.unsynced))) {
                currentState.setText(R.string.unsynced);
            } else {
                currentState.setText(getString(R.string.positive_leapcard_drawer));
            }
        } else {
            leapNumber = getString(R.string.cash_leapcard_drawer);
            currentState.setText(R.string.cannot_sync_leap_balance);
        }

        barName.invalidate();
        barName.setText(sharedPreferences.getString(getString(R.string.pref_key_name), ""));
        barLeapCardNumber.invalidate();
        barLeapCardNumber.setText(leapNumber);

        //set appropriate state if leap is positive, if cash is being used or anything else
        currentState.invalidate();

        sqLiteDatabase.close();
        cursor.close();
    }

    public static int getCurrentActiveLeap(DatabaseHelper databaseHelper) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        int ID;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ID = Integer.parseInt(cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_ID));
            sqLiteDatabase.close();
            cursor.close();
            return ID;
        }
        sqLiteDatabase.close();
        cursor.close();
        return -1;
    }

    public static void getCurrentBalance(DatabaseHelper databaseHelper, SharedPreferences sharedPreferences, Context context) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        System.out.println("IN CURRENT BAL FUNC");

        if (!sharedPreferences.getBoolean(context.getString(R.string.pref_key_first_sync), false)) {
            //sync first time round - poll balance from online
            Leap leap = new Leap(context);
            leap.scrape();
            databaseHelper.close();
        } else {
            //else we have synced before - find balance online and ask user which to use

            //if synced before but balance is unsynced somehow or blank, resync
            if (sharedPreferences.getString(context.getString(R.string.pref_key_last_synced_balance), "").equals("unsynced")
                    || sharedPreferences.getString(context.getString(R.string.pref_key_last_synced_balance), "").equals("")
                    || sharedPreferences.getString(context.getString(R.string.pref_key_current_balance), "").equals("unsynced")
                    || sharedPreferences.getString(context.getString(R.string.pref_key_current_balance), "").equals("")
                    || sharedPreferences.getString(context.getString(R.string.pref_key_current_balance), "").contains("€")
                    || sharedPreferences.getString(context.getString(R.string.pref_key_last_synced_balance), "").contains("€")){

                System.out.println("in update balance loop!!");

                SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_EXPENDITURES, null);
                double initial = Double.parseDouble(sharedPreferences.getString(context.getResources()
                        .getString(R.string.pref_key_last_synced_balance), "").replace("€", ""));
                double subtotal = initial;
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        if (cursor.getString(DatabaseHelper.COL_EXPENDITURES_TYPE).equals("Leap")) {
                            subtotal -= Double.parseDouble(Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE)));
                        } else if (!cursor.getString(DatabaseHelper.COL_EXPENDITURES_TYPE).equals("cash")) {
                            subtotal += Double.parseDouble(Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE)));
                        }
                        System.out.println(subtotal);
                        cursor.moveToNext();
                    }

                    cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
                    if (Fares.formatDecimals(subtotal).contains("-") && cursor.getCount() > 0) {
                        databaseHelper.modifyActive(Database.LeapLogin.TABLE_NAME, Database.LeapLogin.IS_ACTIVE,
                                Database.LeapLogin.ID, getCurrentActiveLeap(databaseHelper), false);
                        Toast.makeText(context, R.string.negative_bal_pls_topup, Toast.LENGTH_LONG).show();
                    }
                }

                databaseHelper.printTableContents(Database.Expenditures.TABLE_NAME);
                databaseHelper.close();
                sqLiteDatabase.close();
                cursor.close();

                String balance = Fares.formatDecimals(subtotal);
                if (balance.contains("-")) {
                    balance = "-€" + balance.replace("€", "").replace("-", "");
                } else {
                    balance = "€" + balance.replace("€", "").replace("-", "");
                }

                editor.putString(context.getResources().getString(R.string.pref_key_current_balance), balance).apply();

                System.out.println("initial bal " + initial);
                System.out.println("current bal " + balance);
            } else {
                //else if these keys are equal to something and not on first sync...
                System.out.println("In ELSE loop for current bal");
                System.out.println(sharedPreferences.getString(context.getString(R.string.pref_key_current_balance), ""));
                System.out.println(sharedPreferences.getString(context.getString(R.string.pref_key_last_synced_balance), ""));

                editor.putString(sharedPreferences.getString(context.getString(R.string.pref_key_current_balance), ""),
                        sharedPreferences.getString(context.getString(R.string.pref_key_last_synced_balance), "")).apply();
                databaseHelper.close();
            }
        }
    }

    public static String getActiveLeapNumber(DatabaseHelper databaseHelper) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER);
        }
        cursor.close();
        sqLiteDatabase.close();
        return "N/A";
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
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutUs.class));
        } else if (id == R.id.visit_us) {
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

    public void setSelected(String fragName) {
        this.fragName = fragName;
    }

    public String getSelected() {
        return fragName;
    }

    class AsynchronousInterstitial extends AsyncTask<Void, Void, Void> {

        AdRequest adRequest;

        @Override
        protected Void doInBackground(Void... params) {
            interstitialAd = new InterstitialAd(getBaseContext());
            interstitialAd.setAdUnitId(Globals.ADMOB_ID);
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
        boolean isIncomplete = false;
        boolean isToDo = false;

        @Override
        protected void onPreExecute() {
            isToDo = !getActiveLeapNumber(databaseHelper).matches("no active leap");
            if (isToDo) {
                leap = new Leap(getApplicationContext());
                leap.scrape();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            long timer = 0;
            if (isToDo) {
                try {
                    Thread.sleep(Globals.ONE_SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!leap.isSynced()) {
                    System.out.println("NOT synced and has slept " + (timer / 1000) + "/120");
                    try {
                        timer += Globals.ONE_SECOND;
                        Thread.sleep(Globals.ONE_SECOND);
                        if(leap.getIncorrectDetails() >= 3){
                            isIncomplete = true;
                            leap.setSynced(true);
                            break;
                        }
                        if (timer > Globals.SIXTY_SECONDS * 2) {
                            isIncomplete = true;
                            leap.setSynced(true);
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (isToDo) {
                if (!isIncomplete) {
                    if (leap.isSynced()) {
                        editor = sharedPreferences.edit();
                        System.out.println("is synced");
                        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
                        if (cursor.getCount() > 0) {
                            getCurrentBalance(databaseHelper, sharedPreferences, getApplicationContext());
                            String oldSyncCumulative = sharedPreferences.getString(getString(R.string.pref_key_current_balance), "");
                            if (oldSyncCumulative.equals("")) {
                                oldSyncCumulative = sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), "");
                            }
                            if (oldSyncCumulative.equals("")) {
                                oldSyncCumulative = "0";
                            }
                            final String oldSync = oldSyncCumulative;
                            final String newSync = leap.getBalance();

                            if (oldSync.equals(newSync)) {
                                //do nothing -- balances coincide so we don't have to modify anything
                            } else {
                                System.out.println("balances are different");
                                //problem - balances aren't the same, ask the user which to use and allow for input if online is wrong
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(getString(R.string.differing_balances))
                                        .setMessage(getString(R.string.online_bal_leap_reported) + newSync + getString(R.string.local_bal_is) +
                                                oldSync + getString(R.string.is_leap_correct))
                                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(MainActivity.this, getString(R.string.bal_updated_successfully) + leap.getBalance() + ")", Toast.LENGTH_LONG).show();
                                                //leap online is correct, get difference between two values to account as an amend
                                                double balanceAmend = Double.parseDouble(newSync.replace("€", "")) - Double.parseDouble(oldSync.replace("€", ""));
                                                databaseHelper.insertExpenditure("amend", getActiveLeapNumber(databaseHelper), Fares.formatDecimals(balanceAmend));
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //amend this value as it's not correct
                                                final CorrectBalanceDialog correctBalanceDialog = new CorrectBalanceDialog();
                                                correctBalanceDialog.show(MainActivity.this.getFragmentManager(), "AmendBalance");
                                                correctBalanceDialog.setSetBalanceDialogListener(new CorrectBalanceDialog.SetBalanceListener() {
                                                    @Override
                                                    public void onDoneClick(android.app.DialogFragment dialog) {
                                                        if (correctBalanceDialog.getBalance().contains("-")) {
                                                            Toast.makeText(MainActivity.this, getString(R.string.amended_balance_neg) + correctBalanceDialog.getBalance().replace("-", ""), Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(MainActivity.this, getString(R.string.amended_balance_pos) + correctBalanceDialog.getBalance(), Toast.LENGTH_LONG).show();
                                                        }

                                                        String balance;
                                                        if (correctBalanceDialog.getBalance().contains("-")) {
                                                            balance = "-€" + correctBalanceDialog.getBalance().replace("-", "");
                                                        } else {
                                                            balance = "€" + correctBalanceDialog.getBalance();
                                                        }
                                                        editor.putString(getString(R.string.pref_key_current_balance), balance).apply();
                                                        System.out.println("balance amended: " + sharedPreferences.getString(getString(R.string.pref_key_curr_synced_balance), ""));

                                                        double balanceAmend = Double.parseDouble(correctBalanceDialog.getBalance().replace("€", "")) - Double.parseDouble(oldSync.replace("€", ""));
                                                        databaseHelper.insertExpenditure("amend", getActiveLeapNumber(databaseHelper), Fares.formatDecimals(balanceAmend));
                                                    }
                                                });
                                            }
                                        })
                                        .show();
                            }
                        }
                        cursor.close();
                        sqLiteDatabase.close();
                    }
                } else {
                    System.out.println("Incomplete");
                }
            }
        }
    }
}

