package com.syzible.iompar;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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

/**
 * Created by ed on 29/10/15.
 */
public class Expenditures extends Fragment {

    View view;
    String subtotal;

    DatabaseHelper databaseHelper;

    TableLayout tableLayout;
    TextView monthlySubTotal, weeklySubTotal, bestOption;
    Fares fares;
    double total;
    long currentTime, firstDayOfWeek, firstDayOfMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_expenditures, null);

        fares = new Fares();
        databaseHelper = new DatabaseHelper(getContext());

        monthlySubTotal = (TextView) view.findViewById(R.id.monthly_expenditures);
        weeklySubTotal = (TextView) view.findViewById(R.id.weekly_expenditures);
        bestOption = (TextView) view.findViewById(R.id.best_option);
        tableLayout = (TableLayout) view.findViewById(R.id.expenditures_table);

        populateTable();
        calculateTotalMonthlyExpenditure();
        calculateTotalWeeklyExpenditure();

        return view;
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
                " WHERE " + Database.Expenditures.TIME_ADDED + " BETWEEN "
                + firstDayOfMonth + " AND " + currentTime;

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
        cursor.close();
        sqLiteDatabase.close();
        setSubTotal("€" + fares.formatDecimals(String.valueOf(total)));
        monthlySubTotal.setText(getSubTotal());

        //is it worth buying a specific ticket?

        /*cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LEAP_CAPS, null);
        if(Double.parseDouble(monthlySubTotal.getText().toString()) > cursor.getString()){

        }*/
    }

    public void calculateTotalWeeklyExpenditure(){
        //current time time in millis
        currentTime = System.currentTimeMillis();

        //first day of current week time in millis
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        firstDayOfWeek = calendar.getTimeInMillis();
        String query = "SELECT * FROM " + Database.Expenditures.TABLE_NAME +
                " WHERE " + Database.Expenditures.TIME_ADDED + " BETWEEN "
                + firstDayOfWeek + " AND " + currentTime;

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
        cursor.close();
        sqLiteDatabase.close();
        setSubTotal("€" + fares.formatDecimals(String.valueOf(total)));
        weeklySubTotal.setText(getSubTotal());
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

            //category
            final TextView type = new TextView(getContext());
            //set leap if number != cash, else set to cash
            if(cursor.getString(DatabaseHelper.COL_EXPENDITURES_CARD_NUMBER).equals("cash")){
                type.setText("Cash");
            } else {
                type.setText("Leap");
            }
            type.setGravity(Gravity.CENTER);

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
            final String costText = "€" + fares.formatDecimals(cursor.getString(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE));
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
                    if(type.getText().toString().equals("Cash")){
                        messagePaymentType = "cash";
                    } else {
                        messagePaymentType = "Leap";
                    }

                    builder.setTitle("Remove Expenditure")
                            .setMessage("The chosen " + messagePaymentType + " expenditure of " + costText + " will be removed.")
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHelper.removeRecord(Database.Expenditures.TABLE_NAME, Database.Expenditures.ID, row);
                                    tableLayout.invalidate();
                                    populateTable();
                                    calculateTotalMonthlyExpenditure();
                                    calculateTotalWeeklyExpenditure();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    public String formatDate(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        return simpleDateFormat.format(Long.parseLong(time));
    }

    public void setSubTotal(String subtotal){this.subtotal=subtotal;}
    public String getSubTotal(){return subtotal;}
}
