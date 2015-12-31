package com.glassbyte.iompar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ed on 30/12/15.
 */
public class CorrectBalanceDialog extends DialogFragment {

    private SetBalanceListener listener = null;

    EditText balance;
    TextView euro;

    public interface SetBalanceListener{
        void onDoneClick(DialogFragment dialog);
    }

    public void setSetBalanceDialogListener(SetBalanceListener listener){this.listener=listener;}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.amend_leap_bal)
                .setMessage(R.string.ensure_confirm_balance)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDoneClick(CorrectBalanceDialog.this);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        RelativeLayout balanceConfirm = new RelativeLayout(this.getActivity());
        balanceConfirm.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams balanceConfirmParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        balanceConfirm.setLayoutParams(balanceConfirmParams);

        euro = new TextView(this.getActivity());
        euro.setTypeface(null, Typeface.BOLD);
        euro.setText("€");
        euro.setTextSize(28);
        euro.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams dotParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.setMarginStart(getDp(24));
        euro.setLayoutParams(dotParams);
        euro.setId(View.generateViewId());

        balance = new EditText(this.getActivity());
        RelativeLayout.LayoutParams balanceParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        balanceParams.addRule(RelativeLayout.RIGHT_OF, euro.getId());
        balanceParams.setMarginStart(getDp(8));
        balanceParams.setMarginEnd(getDp(32));
        balance.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        balance.setLayoutParams(balanceParams);
        balance.setId(View.generateViewId());

        balanceConfirm.addView(euro);
        balanceConfirm.addView(balance);

        builder.setView(balanceConfirm);
        return builder.create();
    }

    public int getDp(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixels, getActivity().getResources().getDisplayMetrics());
    }

    public String getBalance(){return balance.getText().toString();}
}
