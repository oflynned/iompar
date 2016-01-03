package com.glassbyte.iompar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ed on 29/10/15.
 */
public class Expenditures extends Fragment {

    View view;
    String subtotal;

    DatabaseHelper databaseHelper;

    TableLayout tableLayout;
    TextView monthlySubTotal, weeklySubTotal, dailySubTotal;
    TextView monthlySubTotalCash, weeklySubTotalCash, dailySubTotalCash;
    String dailyCap, weeklyCap;

    Fares fares;
    double total;
    long currentTime, firstDayOfWeek, firstDayOfMonth, firstTimeOfDay;

    SharedPreferences sharedPreferences;
    String fareType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_expenditures, null);

        fares = new Fares();
        databaseHelper = new DatabaseHelper(getContext());

        dailySubTotal = (TextView) view.findViewById(R.id.daily_expenditures);
        weeklySubTotal = (TextView) view.findViewById(R.id.weekly_expenditures);
        monthlySubTotal = (TextView) view.findViewById(R.id.monthly_expenditures);

        dailySubTotalCash = (TextView) view.findViewById(R.id.daily_expenditures_cash);
        monthlySubTotalCash = (TextView) view.findViewById(R.id.monthly_expenditures_cash);
        weeklySubTotalCash = (TextView) view.findViewById(R.id.weekly_expenditures_cash);

        tableLayout = (TableLayout) view.findViewById(R.id.expenditures_table);

        populateTable();
        setCaps();
        calculateTotalDailyExpenditure();
        calculateTotalWeeklyExpenditure();
        calculateTotalMonthlyExpenditure();

        return view;
    }

    public void setCaps(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        fareType = sharedPreferences.getString(getString(R.string.pref_key_fare), "");

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LEAP_CAPS, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (fareType.equals(getString(R.string.adult))) {
                cursor.moveToPosition(0);
                setDailyCap("€" + Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_DAILY)));
                setWeeklyCap("€" + Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_WEEKLY)));
            } else {
                if (fareType.equals(getString(R.string.child))) {
                    cursor.moveToPosition(1);
                    setDailyCap("€" + Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_DAILY)));
                    setWeeklyCap("€" + Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_WEEKLY)));
                } else if (fareType.equals(getString(R.string.student))) {
                    cursor.moveToPosition(2);
                    setDailyCap("€" + Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_DAILY)));
                    setWeeklyCap("€" + Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_WEEKLY)));
                } else {
                    setDailyCap("€0.00");
                    setWeeklyCap("€0.00");
                }
            }
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public void calculateTotalDailyExpenditure(){
        //current time in millis
        currentTime = System.currentTimeMillis();

        //00:00 of current day to filter current expenditures by
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.MILLISECOND);
        firstTimeOfDay = calendar.getTimeInMillis();

        String query = "SELECT * FROM " + Database.Expenditures.TABLE_NAME +
                " WHERE (" + Database.Expenditures.TIME_ADDED + " BETWEEN " +
                firstTimeOfDay + " AND " + currentTime + ") AND (" +
                Database.Expenditures.CARD_NUMBER + " = " + getActiveLeapNumber(databaseHelper) + ") AND (" +
                Database.Expenditures.TYPE + " = 'Leap');";

        total = 0;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                total += Double.parseDouble(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE));
                cursor.moveToNext();
            }
        }
        setSubTotal("€" + Fares.formatDecimals(String.valueOf(total)) + "/" + getDailyCap());
        dailySubTotal.setText(getSubTotal());

        query = "SELECT * FROM " + Database.Expenditures.TABLE_NAME +
                " WHERE (" + Database.Expenditures.TIME_ADDED + " BETWEEN " +
                firstTimeOfDay + " AND " + currentTime + ") AND (" +
                Database.Expenditures.TYPE + " = 'Leap' OR "
                + Database.Expenditures.TYPE + " = 'cash');";

        total = 0;
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery(query, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                total += Double.parseDouble(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE));
                cursor.moveToNext();
            }
        }
        setSubTotal("€" + Fares.formatDecimals(String.valueOf(total)));
        dailySubTotalCash.setText(getSubTotal());

        cursor.close();
        sqLiteDatabase.close();
    }

    public void calculateTotalMonthlyExpenditure(){
        //current time time in millis
        currentTime = System.currentTimeMillis();

        //first day of current week time in millis
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        firstDayOfMonth = calendar.getTimeInMillis();
        String query = "SELECT * FROM " + Database.Expenditures.TABLE_NAME +
                " WHERE (" + Database.Expenditures.TIME_ADDED + " BETWEEN " +
                firstDayOfMonth + " AND " + currentTime + ") AND (" +
                Database.Expenditures.CARD_NUMBER + " = " + getActiveLeapNumber(databaseHelper) + ") AND (" +
                Database.Expenditures.TYPE + " = 'Leap');";

        total = 0;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                total += Double.parseDouble(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE));
                cursor.moveToNext();
            }
        }
        setSubTotal("€" + Fares.formatDecimals(String.valueOf(total)));
        monthlySubTotal.setText(getSubTotal());

        query = "SELECT * FROM " + Database.Expenditures.TABLE_NAME +
                " WHERE (" + Database.Expenditures.TIME_ADDED + " BETWEEN " +
                firstDayOfMonth + " AND " + currentTime + ") AND (" +
                Database.Expenditures.TYPE + " = 'Leap' OR "
                + Database.Expenditures.TYPE + " = 'cash');";

        total = 0;
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery(query, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                total += Double.parseDouble(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE));
                cursor.moveToNext();
            }
        }
        setSubTotal("€" + Fares.formatDecimals(String.valueOf(total)));
        monthlySubTotalCash.setText(getSubTotal());

        cursor.close();
        sqLiteDatabase.close();
    }

    public void calculateTotalWeeklyExpenditure(){
        //current time time in millis
        currentTime = System.currentTimeMillis();

        //first day of current week time in millis
        Calendar weeklyCalendar = Calendar.getInstance(Locale.GERMANY);
        weeklyCalendar.set(Calendar.HOUR_OF_DAY, 0);
        weeklyCalendar.clear(Calendar.MINUTE);
        weeklyCalendar.clear(Calendar.SECOND);
        weeklyCalendar.clear(Calendar.MILLISECOND);
        weeklyCalendar.set(Calendar.DAY_OF_WEEK, weeklyCalendar.getFirstDayOfWeek());
        firstDayOfWeek = weeklyCalendar.getTimeInMillis();

        String query = "SELECT * FROM " + Database.Expenditures.TABLE_NAME +
                " WHERE (" + Database.Expenditures.TIME_ADDED + " BETWEEN " +
                firstDayOfWeek + " AND " + currentTime + ") AND (" +
                Database.Expenditures.CARD_NUMBER + " = " + getActiveLeapNumber(databaseHelper) + ") AND (" +
                Database.Expenditures.TYPE + " = 'Leap');";

        total = 0;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                total += Double.parseDouble(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE));
                cursor.moveToNext();
            }
        }
        setSubTotal("€" + Fares.formatDecimals(String.valueOf(total)) + "/" + getWeeklyCap());
        weeklySubTotal.setText(getSubTotal());

        //overall weekly
        query = "SELECT * FROM " + Database.Expenditures.TABLE_NAME +
                " WHERE (" + Database.Expenditures.TIME_ADDED + " BETWEEN " +
                firstDayOfWeek + " AND " + currentTime + ") AND (" +
                Database.Expenditures.TYPE + " = 'Leap' OR "
                + Database.Expenditures.TYPE + " = 'cash');";

        total = 0;
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery(query, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                total += Double.parseDouble(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE));
                cursor.moveToNext();
            }
        }
        setSubTotal("€" + Fares.formatDecimals(String.valueOf(total)));
        weeklySubTotalCash.setText(getSubTotal());

        cursor.close();
        sqLiteDatabase.close();
    }

    public void populateTable() {
        SQLiteDatabase readDb = databaseHelper.getReadableDatabase();

        final int currNumRows = tableLayout.getChildCount();
        if (currNumRows > 1)
            tableLayout.removeViewsInLayout(1, currNumRows - 1);

        final Cursor cursor = readDb.rawQuery(DatabaseHelper.SELECT_ALL_EXPENDITURES, null);
        int numRows = cursor.getCount();
        System.out.println("Row count " + numRows);
        cursor.moveToFirst();

        for (int i = numRows; i > 0; i--) {
            final TableRow tableRow = new TableRow(getContext());
            TableRow.LayoutParams tableRowParams =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(tableRowParams);

            //assign row via ID instantiation for modify etc
            final int row = cursor.getInt(DatabaseHelper.COL_EXPENDITURES_ID);

            //date
            final TextView timeAdded = new TextView(getContext());
            timeAdded.setText(formatDate(cursor.getString(DatabaseHelper.COL_EXPENDITURES_TIME_ADDED)));
            timeAdded.setGravity(Gravity.CENTER);

            //category - database inserted type gets read here
            final TextView type = new TextView(getContext());
            type.setGravity(Gravity.CENTER);
            switch (cursor.getString(DatabaseHelper.COL_EXPENDITURES_TYPE)){
                case "cash":
                    type.setText(getString(R.string.cash));
                    break;
                case "Leap":
                    type.setText(getString(R.string.leap));
                    break;
                case "topup":
                    type.setText(getString(R.string.topup));
                    break;
                case "amend":
                    type.setText(getString(R.string.amend));
                    break;
            }

            //description
            final TextView leapNumber = new TextView(getContext());
            if(cursor.getString(DatabaseHelper.COL_EXPENDITURES_CARD_NUMBER).equals("cash")){
                leapNumber.setText("N/A");
            } else {
                leapNumber.setText(cursor.getString(DatabaseHelper.COL_EXPENDITURES_CARD_NUMBER));
            }
            leapNumber.setGravity(Gravity.CENTER);

            //description
            final TextView costField = new TextView(getContext());
            String cost;
            if(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE).contains("-")){
                cost = "-€" + Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE).replace("-",""));
            } else {
                cost = "€" + Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE));
            }
            if(cursor.getString(DatabaseHelper.COL_EXPENDITURES_TYPE).equals("cash") ||
                    cursor.getString(DatabaseHelper.COL_EXPENDITURES_TYPE).equals("Leap")){
                cost = "-€" + Fares.formatDecimals(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE));
            }
            final String costText = cost;
            costField.setText(costText);
            costField.setGravity(Gravity.CENTER);

            if (i % 2 == 0) {
                tableRow.setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.colorPrimary));
                timeAdded.setTextColor(Color.WHITE);
                type.setTextColor(Color.WHITE);
                leapNumber.setTextColor(Color.WHITE);
                costField.setTextColor(Color.WHITE);
            }

            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            new ContextThemeWrapper(getContext(), R.style.LongClickDialog));

                    String messagePaymentType;
                    if(type.getText().toString().equals(getString(R.string.cash))){
                        messagePaymentType = getString(R.string.cash_lowercase);
                    } else if(type.getText().toString().equals(getString(R.string.leap))){
                        messagePaymentType = getString(R.string.leap);
                    } else if(type.getText().toString().equals(getString(R.string.topup))){
                        messagePaymentType = getString(R.string.topup);
                    } else {
                        messagePaymentType = getString(R.string.amend);
                    }

                    builder.setTitle(getString(R.string.remove_expenditure))
                            //the chosen expenditure (cash) of €1.39 will be removed
                            //bainfear an caiteachas roghnaithe (airgead) €1.39 de
                            .setMessage(getString(R.string.remove_expenditure_clause_one) + messagePaymentType +
                                    getString(R.string.remove_expenditure_clause_two) + costText +
                                    getString(R.string.remove_expenditure_clause_three))
                            .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ManageLeapCards.updateLocalBalance(databaseHelper, "remove", 0, row);

                            tableLayout.invalidate();
                            populateTable();
                            setCaps();
                            calculateTotalDailyExpenditure();
                            calculateTotalWeeklyExpenditure();
                            calculateTotalMonthlyExpenditure();
                        }
                    })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.create().show();
                    return true;
                }
            });

            //add views
            tableRow.addView(timeAdded);
            tableRow.addView(type);
            tableRow.addView(leapNumber);
            tableRow.addView(costField);

            tableLayout.addView(tableRow);

            cursor.moveToNext();
        }

        cursor.close();
        readDb.close();
        tableLayout.invalidate();
    }

    @SuppressLint("SimpleDateFormat")
    public String formatDate(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        return simpleDateFormat.format(Long.parseLong(time));
    }

    public void setSubTotal(String subtotal){this.subtotal=subtotal;}
    public String getSubTotal(){return subtotal;}
    public void setDailyCap(String dailyCap){this.dailyCap = dailyCap;}
    public String getDailyCap(){return dailyCap;}
    public void setWeeklyCap(String weeklyCap){this.weeklyCap = weeklyCap;}
    public String getWeeklyCap(){return weeklyCap;}
    public static String getActiveLeapNumber(DatabaseHelper databaseHelper){
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            return cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER);
        }
        cursor.close();
        sqLiteDatabase.close();
        return null;
    }
}
