package com.syzible.iompar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

/**
 * Created by ed on 29/10/15.
 */
public class AddExpenditure extends DialogFragment {

    private TextView leapText, cashText, currentBalanceText, currentBalance, costText, cost;
    private boolean hasLeapActive;
    private Switch leapSwitch, cashSwitch;
    private setAddExpenditureListener addExpenditureDialogListener = null;
    private String costBalanceText;
    
    Context context;

    private String depart, arrive;
    private Realtime.LuasDirections enumDirection;

    DatabaseHelper databaseHelper;
    Fares fares = new Fares();

    public AddExpenditure(){}

    public AddExpenditure(String depart, String arrive, Realtime.LuasDirections enumDirection){
        this.depart = depart;
        this.arrive = arrive;
        this.enumDirection = enumDirection;
    }

    //listener that the corresponding button implements
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
        currentBalance.setText("€xx.xx");
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
        databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
        if(cursor.getCount() > 0) {
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
                    leapSwitch.setChecked(true);
                    costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                            context, "leap");
                    cost.setText(costBalanceText);
                    cost.invalidate();
                } else {
                    leapSwitch.setChecked(false);
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
                    cashSwitch.setChecked(false);
                    costBalanceText = "€" + fares.getZoneTraversal(enumDirection, depart, arrive,
                            context, "leap");
                    cost.setText(costBalanceText);
                    cost.invalidate();
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
                        double cashCost = Double.parseDouble(fares.formatDecimals(fares.getZoneTraversal(enumDirection, depart, arrive,
                                context, "cash")));
                        double leapCost = Double.parseDouble(fares.formatDecimals(fares.getZoneTraversal(enumDirection, depart, arrive,
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
                                    databaseHelper.insertExpenditure(true,
                                            traverseCardNumber.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER),
                                            fares.formatDecimals(fares.getZoneTraversal(enumDirection, depart, arrive,
                                                    context, "leap")));
                                    Toast.makeText(context, R.string.expenditure_added_successfully, Toast.LENGTH_SHORT).show();
                                } else {
                                    new AlertDialog.Builder(context)
                                            .setTitle(context.getString(R.string.no_active_leap))
                                            .setMessage(context.getString(R.string.no_active_leap_body) +
                                                    fares.formatDecimals(String.valueOf(difference)) + " " + context.getString(R.string.more))
                                            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //cash payment, no active leap
                                                    databaseHelper.insertExpenditure(false, "cash",
                                                            fares.formatDecimals(fares.getZoneTraversal(enumDirection, depart, arrive,
                                                                    context, "cash")));
                                                    Toast.makeText(context, R.string.expenditure_added_successfully, Toast.LENGTH_SHORT).show();
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
                                databaseHelper.insertExpenditure(false, "cash",
                                        fares.formatDecimals(fares.getZoneTraversal(enumDirection, depart, arrive,
                                                context, "cash")));
                                Toast.makeText(context, R.string.expenditure_added_successfully, Toast.LENGTH_SHORT).show();
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

        if(isLeapActive()){
            leapSwitch.setChecked(true);
            cashSwitch.setChecked(false);
        } else {
            leapSwitch.setChecked(false);
            cashSwitch.setChecked(true);
        }

        return builder.create();
    }

    public int getDp(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixels, context.getResources().getDisplayMetrics());
    }

    public void setLeapActive(boolean hasLeapActive){this.hasLeapActive = hasLeapActive;}
    public boolean isLeapActive(){return hasLeapActive;}

}
