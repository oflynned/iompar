package com.glassbyte.iompar;

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

/**
 * Created by ed on 29/10/15.
 */

public class AddLeapCard extends DialogFragment {

    private EditText leapNumberField, usernameField, emailField, passwordField;
    private CheckBox transformPassword;
    private setAddLeapListener addLeapDialogListener = null;

    public interface setAddLeapListener {
        void onDoneClick(DialogFragment dialogFragment);
    }

    public void setAddLeapDialogListener(setAddLeapListener addLeapDialogListener) {
        this.addLeapDialogListener = addLeapDialogListener;
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
        builder.setTitle(getString(R.string.add_leap))
                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (addLeapDialogListener != null) {
                            addLeapDialogListener.onDoneClick(AddLeapCard.this);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        RelativeLayout propertiesEntry = new RelativeLayout(this.getActivity());
        propertiesEntry.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams propertiesEntryParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        propertiesEntry.setLayoutParams(propertiesEntryParams);

        leapNumberField = new EditText(this.getActivity());
        RelativeLayout.LayoutParams leapNumberParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        leapNumberParams.addRule(RelativeLayout.ALIGN_START, RelativeLayout.TRUE);
        leapNumberParams.setMarginStart(getDp(16));
        leapNumberParams.setMarginEnd(getDp(64));
        leapNumberField.setSingleLine();
        leapNumberField.setInputType(InputType.TYPE_CLASS_NUMBER);
        leapNumberField.setHint(getString(R.string.leap_card_number));
        leapNumberField.setLayoutParams(leapNumberParams);
        leapNumberField.setId(View.generateViewId());

        usernameField = new EditText(this.getActivity());
        RelativeLayout.LayoutParams usernameParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        usernameParams.addRule(RelativeLayout.BELOW, leapNumberField.getId());
        usernameParams.setMarginStart(getDp(16));
        usernameParams.setMarginEnd(getDp(64));
        usernameField.setSingleLine();
        usernameField.setInputType(InputType.TYPE_CLASS_TEXT);
        usernameField.setHint(R.string.leap_card_username);
        usernameField.setLayoutParams(usernameParams);
        usernameField.setId(View.generateViewId());

        emailField = new EditText(this.getActivity());
        RelativeLayout.LayoutParams emailParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        emailParams.addRule(RelativeLayout.BELOW, usernameField.getId());
        emailParams.setMarginStart(getDp(16));
        emailParams.setMarginEnd(getDp(64));
        emailField.setSingleLine();
        emailField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailField.setHint(R.string.leap_card_email);
        emailField.setLayoutParams(emailParams);
        emailField.setId(View.generateViewId());

        passwordField = new EditText(this.getActivity());
        RelativeLayout.LayoutParams passwordParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        passwordParams.addRule(RelativeLayout.BELOW, emailField.getId());
        passwordParams.setMarginStart(getDp(16));
        passwordParams.setMarginEnd(getDp(64));
        passwordField.setSingleLine();
        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordField.setHint(R.string.leap_card_password);
        passwordField.setLayoutParams(passwordParams);
        passwordField.setId(View.generateViewId());

        transformPassword = new CheckBox(this.getActivity());
        RelativeLayout.LayoutParams transformPasswordParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        transformPasswordParams.addRule(RelativeLayout.ALIGN_PARENT_END, passwordField.getId());
        transformPasswordParams.addRule(RelativeLayout.BELOW, emailField.getId());
        transformPasswordParams.setMarginEnd(getDp(16));
        transformPassword.setChecked(false);
        transformPassword.setLayoutParams(transformPasswordParams);
        transformPassword.setId(View.generateViewId());
        transformPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!transformPassword.isChecked()) {
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordField.invalidate();
                } else {
                    passwordField.setTransformationMethod(null);
                    passwordField.invalidate();
                }
            }
        });

        propertiesEntry.addView(leapNumberField);
        propertiesEntry.addView(usernameField);
        propertiesEntry.addView(emailField);
        propertiesEntry.addView(passwordField);
        propertiesEntry.addView(transformPassword);

        builder.setView(propertiesEntry);

        return builder.create();
    }

    public int getDp(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixels, getContext().getResources().getDisplayMetrics());
    }

    public String getNumberField(){return leapNumberField.getText().toString();}
    public String getUsernameField(){return usernameField.getText().toString();}
    public String getEmailField(){return emailField.getText().toString();}
    public String getPasswordField(){return passwordField.getText().toString();}
}
