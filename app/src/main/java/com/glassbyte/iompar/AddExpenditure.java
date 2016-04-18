package com.glassbyte.iompar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ed on 29/10/15.
 */
public class AddExpenditure extends DialogFragment {

    private TextView leapText, cashText, currentBalanceText, currentBalance, costText, cost;
    private boolean hasLeapActive;
    private Switch leapSwitch, cashSwitch;
    private setAddExpenditureListener addExpenditureDialogListener = null;
    private String costBalanceText;
    private double dailyCap, weeklyCap;

    Context context;

    private String depart, arrive;
    private Realtime.LuasDirections enumDirection;

    DatabaseHelper databaseHelper;
    Fares fares = new Fares();
    Expenditures expenditures;

    public AddExpenditure() {}

    @SuppressLint("ValidFragment")
    public AddExpenditure(String depart, String arrive, Realtime.LuasDirections enumDirection) {
        this.depart = depart;
        this.arrive = arrive;
        this.enumDirection = enumDirection;
    }

    //listener that the corresponding button implements
    //void in our case as everything is handled in this class for determining caps
    public interface setAddExpenditureListener {
        void onDoneClick(DialogFragment dialogFragment);
    }

    public void setAddExpenditureDialogListener(setAddExpenditureListener addExpenditureDialogListener) {
        this.addExpenditureDialogListener = addExpenditureDialogListener;
    }

    /**
     * onCreateDialog is a generic builder for generating a dialog
     * per row id given, such that tasks can be added to the db
     * @param savedInstanceState the parsed data for the given context
     * @return the appropriate dialog
     */
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        expenditures = new Expenditures();
        databaseHelper = new DatabaseHelper(context);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currBalance;

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            if(!sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("")){
                if(sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").contains("-")) {
                    currBalance = "-€" + Fares.formatDecimals(sharedPreferences.getString(getString(R.string.pref_key_current_balance), "")).replace("-","");
                } else {
                    currBalance = "€" + Fares.formatDecimals(sharedPreferences.getString(getString(R.string.pref_key_current_balance), ""));
                }
            } else if(!sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), "").equals("")){
                if(sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), "").contains("-")) {
                    currBalance = "-€" + Fares.formatDecimals(sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), "")).replace("-", "");
                } else {
                    currBalance = "€" + Fares.formatDecimals(sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), ""));
                }
            } else {
                currBalance = getString(R.string.unsynced);
            }
        } else {
            currBalance = getString(R.string.using_cash);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        RelativeLayout propertiesEntry = new RelativeLayout(context);
        propertiesEntry.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams propertiesEntryParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        propertiesEntry.setLayoutParams(propertiesEntryParams);

        leapText = new TextView(context);
        RelativeLayout.LayoutParams leapTextParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        leapTextParams.setMargins(getDp(24), getDp(8), 0, 0);
        leapText.setText(R.string.leap_payment);
        leapText.setLayoutParams(leapTextParams);
        leapText.setId(View.generateViewId());

        leapSwitch = new Switch(context);
        RelativeLayout.LayoutParams leapSwitchParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        leapSwitchParams.addRule(RelativeLayout.ALIGN_PARENT_END, leapText.getId());
        leapSwitchParams.setMarginEnd(getDp(16));
        leapSwitch.setChecked(false);
        leapSwitch.setLayoutParams(leapSwitchParams);
        leapSwitch.setId(View.generateViewId());

        if(sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").contains("-")){
            leapSwitch.setClickable(false);
        }

        cashText = new TextView(context);
        RelativeLayout.LayoutParams cashTextParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        cashTextParams.addRule(RelativeLayout.BELOW, leapText.getId());
        cashTextParams.setMargins(getDp(24), getDp(8), 0, 0);
        cashText.setText(R.string.cash_payment);
        cashText.setLayoutParams(cashTextParams);
        cashText.setId(View.generateViewId());

        cashSwitch = new Switch(context);
        RelativeLayout.LayoutParams cashSwitchParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        cashSwitchParams.addRule(RelativeLayout.ALIGN_PARENT_END, cashText.getId());
        cashSwitchParams.addRule(RelativeLayout.BELOW, leapSwitch.getId());
        cashSwitchParams.setMarginEnd(getDp(16));
        cashSwitch.setChecked(false);
        cashSwitch.setLayoutParams(cashSwitchParams);
        cashSwitch.setId(View.generateViewId());

        currentBalanceText = new TextView(context);
        RelativeLayout.LayoutParams currentBalanceTextParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        currentBalanceTextParams.addRule(RelativeLayout.BELOW, cashText.getId());
        currentBalanceTextParams.setMargins(getDp(24), getDp(8), 0, 0);
        currentBalanceText.setText(R.string.current_leap_balance);
        currentBalanceText.setLayoutParams(currentBalanceTextParams);
        currentBalanceText.setId(View.generateViewId());

        currentBalance = new TextView(context);
        RelativeLayout.LayoutParams currentBalanceParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        currentBalanceParams.addRule(RelativeLayout.BELOW, cashSwitch.getId());
        currentBalanceParams.addRule(RelativeLayout.ALIGN_PARENT_END, currentBalanceText.getId());
        currentBalanceParams.setMargins(0, getDp(8), getDp(24), 0);
        //current leap card balance
        currentBalance.setText(currBalance);
        currentBalance.setLayoutParams(currentBalanceParams);
        currentBalance.setId(View.generateViewId());

        costText = new TextView(context);
        RelativeLayout.LayoutParams costTextParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        costTextParams.addRule(RelativeLayout.BELOW, currentBalanceText.getId());
        costTextParams.setMargins(getDp(24), getDp(8), 0, 0);
        costText.setText(R.string.cost_of_journey);
        costText.setLayoutParams(costTextParams);
        costText.setId(View.generateViewId());

        //move to active row
        if (cursor.getCount() > 0) {
            setLeapActive(true);
            costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                    context, "leap");
        } else {
            setLeapActive(false);
            costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                    context, "cash");
        }
        cursor.close();
        sqLiteDatabase.close();

        cost = new TextView(context);
        RelativeLayout.LayoutParams costParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        costParams.addRule(RelativeLayout.BELOW, currentBalance.getId());
        costParams.addRule(RelativeLayout.ALIGN_PARENT_END, costText.getId());
        costParams.setMargins(0, getDp(8), getDp(24), 0);
        cost.setLayoutParams(costParams);
        cost.setText(costBalanceText);
        cost.setId(View.generateViewId());

        cashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!cashSwitch.isChecked()) {
                    if((!sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("")
                        || !sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals(""))
                            && !sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").contains("-")){
                        System.out.println("loop 1");
                        //if first and subsequent syncs have occurred where the latest sync is not negative
                        leapSwitch.setChecked(true);
                        cashSwitch.setChecked(false);
                        costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                                context, "leap");
                        cost.setText(costBalanceText);
                        cost.invalidate();
                    } else if((!sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("")
                            || !sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals(""))
                            && !sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").contains("-")){
                        System.out.println("loop 2");
                        //if on first sync and not negative and no subsequent syncs
                        leapSwitch.setChecked(true);
                        cashSwitch.setChecked(false);
                        costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                                context, "leap");
                        cost.setText(costBalanceText);
                        cost.invalidate();
                    } else {
                        System.out.println("loop 3");
                        //else the balance is negative and cash should be the only option
                        Toast.makeText(getContext(), R.string.negative_balance_warning, Toast.LENGTH_LONG).show();
                        leapSwitch.setChecked(false);
                        cashSwitch.setChecked(true);
                        costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                                context, "cash");
                        cost.setText(costBalanceText);
                        cost.invalidate();
                    }
                } else {
                    leapSwitch.setChecked(false);
                    cashSwitch.setChecked(true);
                    costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                            context, "cash");
                    cost.setText(costBalanceText);
                    cost.invalidate();
                }
            }
        });

        leapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!leapSwitch.isChecked()) {
                    cashSwitch.setChecked(true);
                    costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                            context, "cash");
                    cost.setText(costBalanceText);
                    cost.invalidate();

                } else {
                    //leap was selected where cash was unselected, but Leap may be
                    //negative where the switch must be frozen and cash enabled
                    if((!sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("")
                            || !sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals(""))
                            && !sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").contains("-")){
                        System.out.println("loop 4");
                        //if first and subsequent syncs have occurred where the latest sync is not negative
                        leapSwitch.setChecked(true);
                        cashSwitch.setChecked(false);
                        costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                                context, "leap");
                        cost.setText(costBalanceText);
                        cost.invalidate();
                    } else if(!sharedPreferences.getString(getString(R.string.pref_key_current_balance),"").matches("")
                            && !sharedPreferences.getString(getString(R.string.pref_key_current_balance),"").contains("-")
                            && !sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance),"").contains("-")){
                        System.out.println("loop 5");
                        //if on first sync and not negative and no subsequent syncs
                        leapSwitch.setChecked(true);
                        cashSwitch.setChecked(false);
                        costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                                context, "leap");
                        cost.setText(costBalanceText);
                        cost.invalidate();
                    } else {
                        System.out.println("loop 6");
                        //else the balance is negative and cash should be the only option
                        if(cursor.getCount() > 0){
                            Toast.makeText(getContext(), R.string.negative_balance_warning, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), R.string.add_leap_cheaper_prices_warning, Toast.LENGTH_LONG).show();
                        }
                        leapSwitch.setChecked(false);
                        cashSwitch.setChecked(true);
                        costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                                context, "cash");
                        cost.setText(costBalanceText);
                        cost.invalidate();
                    }
                }
            }
        });

        propertiesEntry.addView(leapText);
        propertiesEntry.addView(leapSwitch);
        propertiesEntry.addView(cashText);
        propertiesEntry.addView(cashSwitch);
        propertiesEntry.addView(currentBalanceText);
        propertiesEntry.addView(currentBalance);
        propertiesEntry.addView(costText);
        propertiesEntry.addView(cost);

        builder.setTitle(context.getString(R.string.add_expenditure))
                .setPositiveButton(context.getString(R.string.add), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        double cashCost = Double.parseDouble(Fares.formatDecimals(fares.getZoneTraversal(enumDirection, depart, arrive,
                                context, "cash")));
                        double leapCost = Double.parseDouble(Fares.formatDecimals(fares.getZoneTraversal(enumDirection, depart, arrive,
                                context, "leap")));
                        double difference = cashCost - leapCost;

                        if (addExpenditureDialogListener != null) {
                            addExpenditureDialogListener.onDoneClick(AddExpenditure.this);
                            if (leapSwitch.isChecked()) {
                                SQLiteDatabase readCardNumber = databaseHelper.getReadableDatabase();
                                Cursor traverseCardNumber = readCardNumber.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
                                if (traverseCardNumber.getCount() > 0) {
                                    //insert leap expenditure
                                    traverseCardNumber.moveToFirst();

                                    //check daily & weekly caps, if amount > daily, add difference
                                    //if weekly has been reached, free travel for this week

                                    double currentDailyExpenditure = 0, currentWeeklyExpenditure = 0;
                                    double overshoot, cumulative;
                                    long currentTime, firstTimeOfDay, firstTimeOfWeek;

                                    setCaps();

                                    //current time in millis
                                    currentTime = System.currentTimeMillis();

                                    //00:00 of current day to filter current expenditures by
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                                    calendar.clear(Calendar.MINUTE);
                                    calendar.clear(Calendar.MILLISECOND);
                                    firstTimeOfDay = calendar.getTimeInMillis();

                                    //first day of current week time in millis
                                    Calendar weeklyCalendar = Calendar.getInstance(Locale.GERMANY);
                                    weeklyCalendar.set(Calendar.HOUR_OF_DAY, 0);
                                    weeklyCalendar.clear(Calendar.MINUTE);
                                    weeklyCalendar.clear(Calendar.SECOND);
                                    weeklyCalendar.clear(Calendar.MILLISECOND);
                                    weeklyCalendar.set(Calendar.DAY_OF_WEEK, weeklyCalendar.getFirstDayOfWeek());
                                    firstTimeOfWeek = weeklyCalendar.getTimeInMillis();

                                    String query = "SELECT * FROM " + Database.Expenditures.TABLE_NAME +
                                            " WHERE (" + Database.Expenditures.TIME_ADDED + " BETWEEN " +
                                            firstTimeOfDay + " AND " + currentTime + ") AND (" +
                                            Database.Expenditures.CARD_NUMBER + " = " +
                                            MainActivity.getActiveLeapNumber(databaseHelper) + ") AND (" +
                                            Database.Expenditures.TYPE + " = 'Leap');";

                                    SQLiteDatabase expensesDB = databaseHelper.getReadableDatabase();
                                    Cursor expensesCursor = expensesDB.rawQuery(query, null);
                                    if (expensesCursor.getCount() > 0) {
                                        expensesCursor.moveToFirst();
                                        for (int i = 0; i < expensesCursor.getCount(); i++) {
                                            currentDailyExpenditure += expensesCursor
                                                    .getDouble(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE);
                                            expensesCursor.moveToNext();
                                            System.out.println(currentDailyExpenditure);
                                        }
                                    } else {
                                        currentDailyExpenditure = 0;
                                    }

                                    String weeklyQuery = "SELECT * FROM " + Database.Expenditures.TABLE_NAME +
                                            " WHERE (" + Database.Expenditures.TIME_ADDED + " BETWEEN " +
                                            firstTimeOfWeek + " AND " + currentTime + ") AND (" +
                                            Database.Expenditures.CARD_NUMBER + " = " +
                                            MainActivity.getActiveLeapNumber(databaseHelper) + ") AND (" +
                                            Database.Expenditures.TYPE + " = 'Leap');";

                                    SQLiteDatabase weeklyExpensesDB = databaseHelper.getReadableDatabase();
                                    Cursor weeklyExpensesCursor = weeklyExpensesDB.rawQuery(weeklyQuery, null);
                                    if (weeklyExpensesCursor.getCount() > 0) {
                                        weeklyExpensesCursor.moveToFirst();
                                        for (int i = 0; i < weeklyExpensesCursor.getCount(); i++) {
                                            System.out.println("CURRENT COUNT " + i + "/" + weeklyExpensesCursor.getCount());
                                            currentWeeklyExpenditure += weeklyExpensesCursor
                                                    .getDouble(DatabaseHelper.COL_EXPENDITURES_EXPENDITURE);
                                            System.out.println(currentWeeklyExpenditure);
                                            weeklyExpensesCursor.moveToNext();
                                        }
                                    } else {
                                        System.out.println("else loop defaulted");
                                        currentWeeklyExpenditure = 0;
                                    }

                                    cumulative = currentDailyExpenditure + leapCost;

                                    //if less than weekly cap
                                    if (currentWeeklyExpenditure < getWeeklyCap()) {
                                        System.out.println("currently weekly exp < weekly cap");
                                        if ((currentWeeklyExpenditure + leapCost) < getWeeklyCap()) {
                                            //if greater than or equal to daily cap -> free
                                            if (currentDailyExpenditure >= getDailyCap()) {
                                                System.out.println("currently daily exp >= daily cap");
                                                databaseHelper.insertExpenditure("Leap",
                                                        traverseCardNumber.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER),
                                                        Fares.formatDecimals(String.valueOf(0)));
                                                Toast.makeText(getContext(), R.string.daily_cap_reached, Toast.LENGTH_LONG).show();
                                            } else {
                                                System.out.println("currently daily exp < daily cap");
                                                //else if less than cap and expenditure puts amounts over cap
                                                //find distance to cap and add as expenditure
                                                if (cumulative > getDailyCap()) {
                                                    overshoot = getDailyCap() - currentDailyExpenditure;
                                                    if (overshoot < 0) {
                                                        overshoot = overshoot * -1;
                                                    }

                                                    databaseHelper.insertExpenditure("Leap",
                                                            traverseCardNumber.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER),
                                                            Fares.formatDecimals(String.valueOf(overshoot)));

                                                    Toast.makeText(getContext(), context.getString(R.string.daily_cap_reached_paid) +
                                                            Fares.formatDecimals(String.valueOf(overshoot)) +
                                                            context.getString(R.string.paid_for_transit), Toast.LENGTH_LONG).show();
                                                } else {
                                                    //else under the cap and just add raw expenditure to table
                                                    System.out.println("Entered else loop? - inserting regular leap exp");
                                                    databaseHelper.insertExpenditure("Leap",
                                                            traverseCardNumber.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER),
                                                            Fares.formatDecimals(String.valueOf(leapCost)));
                                                    Toast.makeText(context, R.string.expenditure_added_successfully, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } else {
                                            System.out.println("currently weekly exp + exp > weekly cap");
                                            //if cumulative is GREATER than currently weekly expenditure
                                            overshoot = getWeeklyCap() - currentWeeklyExpenditure;
                                            databaseHelper.insertExpenditure("Leap",
                                                    traverseCardNumber.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER),
                                                    Fares.formatDecimals(String.valueOf(overshoot)));
                                            Toast.makeText(getContext(), context.getString(R.string.weekly_cap_reached) +
                                                    Fares.formatDecimals(String.valueOf(overshoot)) +
                                                    context.getString(R.string.paid_for_transit), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        System.out.println("currently weekly exp >= weekly cap");
                                        //else over weekly cap, therefore free transportation even if daily cap not reached
                                        databaseHelper.insertExpenditure("Leap",
                                                traverseCardNumber.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER),
                                                Fares.formatDecimals(String.valueOf(0)));
                                        Toast.makeText(getContext(), R.string.weekly_cap_reached_free_transit, Toast.LENGTH_LONG).show();
                                    }

                                    weeklyExpensesCursor.close();
                                    weeklyExpensesDB.close();
                                    expensesDB.close();
                                    expensesCursor.close();
                                } else {
                                    new AlertDialog.Builder(context)
                                            .setTitle(context.getString(R.string.no_active_leap))
                                            .setMessage(context.getString(R.string.no_active_leap_body) +
                                                    Fares.formatDecimals(String.valueOf(difference)) + " " + context.getString(R.string.more))
                                            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //cash payment, no active leap
                                                    databaseHelper.insertExpenditure("cash", "cash",
                                                            Fares.formatDecimals(fares.getZoneTraversal(enumDirection, depart, arrive, context, "cash")));
                                                    Toast.makeText(context, R.string.expenditure_added_successfully, Toast.LENGTH_LONG).show();
                                                }
                                            })
                                            .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .create()
                                            .show();
                                }
                                readCardNumber.close();
                                traverseCardNumber.close();
                            } else {
                                //cash payment
                                databaseHelper.insertExpenditure("cash", "cash",
                                        Fares.formatDecimals(fares.getZoneTraversal(enumDirection, depart, arrive, context, "cash")));
                                Toast.makeText(context, R.string.expenditure_added_successfully, Toast.LENGTH_LONG).show();
                            }
                            databaseHelper.printTableContents(Database.Expenditures.TABLE_NAME);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.setView(propertiesEntry);

        if (isLeapActive()) {
            leapSwitch.setChecked(true);
            cashSwitch.setChecked(false);
        } else {
            leapSwitch.setChecked(false);
            cashSwitch.setChecked(true);
        }
        databaseHelper.close();
        return builder.create();
    }

    public void setCaps() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fareType = sharedPreferences.getString(getString(R.string.pref_key_fare), "");

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LEAP_CAPS, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (fareType.equals(getString(R.string.adult))) {
                cursor.moveToPosition(0);
                setDailyCap(cursor.getDouble(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_DAILY));
                setWeeklyCap(cursor.getDouble(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_WEEKLY));
            } else if (fareType.equals(getString(R.string.child))) {
                cursor.moveToPosition(1);
                setDailyCap(cursor.getDouble(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_DAILY));
                setWeeklyCap(cursor.getDouble(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_WEEKLY));
            } else if (fareType.equals(getString(R.string.student))) {
                cursor.moveToPosition(2);
                setDailyCap(cursor.getDouble(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_DAILY));
                setWeeklyCap(cursor.getDouble(DatabaseHelper.COL_LUAS_LEAP_CAPS_LUAS_WEEKLY));
            } else {
                setDailyCap(0);
                setWeeklyCap(0);
            }
        }
        databaseHelper.close();
        cursor.close();
        sqLiteDatabase.close();
    }

    public int getDp(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixels, context.getResources().getDisplayMetrics());
    }

    public void setLeapActive(boolean hasLeapActive) {
        this.hasLeapActive = hasLeapActive;
    }
    public boolean isLeapActive() {
        return hasLeapActive;
    }

    @Deprecated
    public void setDailyCap(double dailyCap) {
        this.dailyCap = dailyCap;
    }

    @Deprecated
    public double getDailyCap() {
        return dailyCap;
    }

    @Deprecated
    public void setWeeklyCap(double weeklyCap) {
        this.weeklyCap = weeklyCap;
    }

    @Deprecated
    public double getWeeklyCap() {
        return weeklyCap;
    }

}
