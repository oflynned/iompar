package com.syzible.iompar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ed on 29/10/15.
 */
public class AddExpenditure extends DialogFragment {

    private TextView leapText, cashText, currentBalanceText, currentBalance, costText, cost;
    private Switch leapSwitch, cashSwitch;
    private setAddExpenditureListener addExpenditureDialogListener = null;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Expenditure")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (addExpenditureDialogListener != null) {
                            addExpenditureDialogListener.onDoneClick(AddExpenditure.this);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        RelativeLayout propertiesEntry = new RelativeLayout(this.getActivity());
        propertiesEntry.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams propertiesEntryParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        propertiesEntry.setLayoutParams(propertiesEntryParams);

        leapText = new TextView(this.getActivity());
        RelativeLayout.LayoutParams leapTextParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        leapTextParams.setMargins(getDp(24), getDp(8), 0, 0);
        leapText.setText("Leap Payment");
        leapText.setLayoutParams(leapTextParams);
        leapText.setId(View.generateViewId());

        leapSwitch = new Switch(this.getActivity());
        RelativeLayout.LayoutParams leapSwitchParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        leapSwitchParams.addRule(RelativeLayout.ALIGN_PARENT_END, leapText.getId());
        leapSwitchParams.setMarginEnd(getDp(16));
        leapSwitch.setChecked(false);
        leapSwitch.setLayoutParams(leapSwitchParams);
        leapSwitch.setId(View.generateViewId());
        leapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!leapSwitch.isChecked()) {
                    Toast.makeText(getContext(), "!checked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "checked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cashText = new TextView(this.getActivity());
        RelativeLayout.LayoutParams cashTextParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        cashTextParams.addRule(RelativeLayout.BELOW, leapText.getId());
        cashTextParams.setMargins(getDp(24), getDp(8), 0, 0);
        cashText.setText("Cash Payment");
        cashText.setLayoutParams(cashTextParams);
        cashText.setId(View.generateViewId());

        cashSwitch = new Switch(this.getActivity());
        RelativeLayout.LayoutParams cashSwitchParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cashSwitchParams.addRule(RelativeLayout.ALIGN_PARENT_END, cashText.getId());
        cashSwitchParams.addRule(RelativeLayout.BELOW, leapSwitch.getId());
        cashSwitchParams.setMarginEnd(getDp(16));
        cashSwitch.setChecked(false);
        cashSwitch.setLayoutParams(cashSwitchParams);
        cashSwitch.setId(View.generateViewId());
        cashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!cashSwitch.isChecked()) {
                    Toast.makeText(getContext(), "!checked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "checked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        currentBalanceText = new TextView(this.getActivity());
        RelativeLayout.LayoutParams currentBalanceTextParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        currentBalanceTextParams.addRule(RelativeLayout.BELOW, cashText.getId());
        currentBalanceTextParams.setMargins(getDp(24), getDp(8), 0, 0);
        currentBalanceText.setText("Current Leap Balance:");
        currentBalanceText.setLayoutParams(currentBalanceTextParams);
        currentBalanceText.setId(View.generateViewId());

        currentBalance = new TextView(this.getActivity());
        RelativeLayout.LayoutParams currentBalanceParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        currentBalanceParams.addRule(RelativeLayout.BELOW, cashSwitch.getId());
        currentBalanceParams.addRule(RelativeLayout.ALIGN_PARENT_END, currentBalanceText.getId());
        currentBalanceParams.setMargins(0, getDp(8), getDp(24), 0);
        currentBalance.setText("€xx.xx");
        currentBalance.setLayoutParams(currentBalanceParams);
        currentBalance.setId(View.generateViewId());

        costText = new TextView(this.getActivity());
        RelativeLayout.LayoutParams costTextParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        costTextParams.addRule(RelativeLayout.BELOW, currentBalanceText.getId());
        costTextParams.setMargins(getDp(24), getDp(8), 0, 0);
        costText.setText("Cost of this Journey:");
        costText.setLayoutParams(costTextParams);
        costText.setId(View.generateViewId());

        cost = new TextView(this.getActivity());
        RelativeLayout.LayoutParams costParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        costParams.addRule(RelativeLayout.BELOW, currentBalance.getId());
        costParams.addRule(RelativeLayout.ALIGN_PARENT_END, costText.getId());
        costParams.setMargins(0, getDp(8), getDp(24), 0);
        cost.setText("€xx.xx");
        cost.setLayoutParams(costParams);
        cost.setId(View.generateViewId());

        propertiesEntry.addView(leapText);
        propertiesEntry.addView(leapSwitch);
        propertiesEntry.addView(cashText);
        propertiesEntry.addView(cashSwitch);
        propertiesEntry.addView(currentBalanceText);
        propertiesEntry.addView(currentBalance);
        propertiesEntry.addView(costText);
        propertiesEntry.addView(cost);

        builder.setView(propertiesEntry);

        return builder.create();
    }

    public int getDp(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixels, getContext().getResources().getDisplayMetrics());
    }
}
