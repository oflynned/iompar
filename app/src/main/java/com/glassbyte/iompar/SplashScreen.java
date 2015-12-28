package com.glassbyte.iompar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

/**
 * Created by ed on 27/12/15.
 */
public class SplashScreen extends Activity {

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    Fares fares = new Fares();

    boolean synced = false;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        AsynchronousFareRetrieval asynchronousFareRetrieval = new AsynchronousFareRetrieval();
        asynchronousFareRetrieval.execute();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread splash = new Thread() {
            public void run() {
                try {
                    while (!synced) {
                        sleep(100);
                    }
                    if (synced) {
                        startActivity(new Intent("com.glassbyte.iompar.ClearScreen"));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }

            }
        };
        splash.start();
    }

    public void setFares() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
        cursor.moveToFirst();
        int count = cursor.getCount();

        //single fares table
        if (count > 0) {
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
            } catch (IOException e) {
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

                    System.out.println("Single Zone " + (j - 1) + ": "
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

                    System.out.println("Return Zone " + (j - 7) + ": "
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
            synced = true;
        }
    }
}
