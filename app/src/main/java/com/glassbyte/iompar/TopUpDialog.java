package com.glassbyte.iompar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ed on 30/12/15.
 */
@Deprecated
public class TopUpDialog extends DialogFragment {

    private SetTopUpListener listener = null;
    private NumberPicker hundred, ten, one, oneTenth, oneHundredth;

    public interface SetTopUpListener{
        void onDoneClick(DialogFragment dialog);
    }

    public void setSetTopUpDialogListener(SetTopUpListener listener){this.listener=listener;}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.top_up_leap)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDoneClick(TopUpDialog.this);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        RelativeLayout percentagePicker = new RelativeLayout(this.getActivity());
        percentagePicker.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams percentagePickerParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        percentagePicker.setLayoutParams(percentagePickerParams);

        hundred = new NumberPicker(this.getActivity());
        hundred.setMinValue(0);
        hundred.setMaxValue(9);
        RelativeLayout.LayoutParams hundredParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hundredParams.addRule(RelativeLayout.ALIGN_START, RelativeLayout.TRUE);
        hundred.setLayoutParams(hundredParams);
        hundred.setId(View.generateViewId());

        ten = new NumberPicker(this.getActivity());
        ten.setMinValue(0);
        ten.setMaxValue(9);
        RelativeLayout.LayoutParams tenParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tenParams.addRule(RelativeLayout.RIGHT_OF, hundred.getId());
        ten.setLayoutParams(tenParams);
        ten.setId(View.generateViewId());

        one = new NumberPicker(this.getActivity());
        one.setMinValue(0);
        one.setMaxValue(9);
        RelativeLayout.LayoutParams oneParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        oneParams.addRule(RelativeLayout.RIGHT_OF, ten.getId());
        one.setLayoutParams(oneParams);
        one.setId(View.generateViewId());

        TextView dot = new TextView(this.getActivity());
        dot.setTypeface(null, Typeface.BOLD);
        dot.setText(".");
        dot.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams dotParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.addRule(RelativeLayout.RIGHT_OF, one.getId());
        dotParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        dot.setLayoutParams(dotParams);
        dot.setId(View.generateViewId());

        oneTenth = new NumberPicker(this.getActivity());
        oneTenth.setMinValue(0);
        oneTenth.setMaxValue(9);
        RelativeLayout.LayoutParams oneTenthParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        oneTenthParams.addRule(RelativeLayout.RIGHT_OF, dot.getId());
        oneTenth.setLayoutParams(oneTenthParams);
        oneTenth.setId(View.generateViewId());

        oneHundredth = new NumberPicker(this.getActivity());
        oneHundredth.setMinValue(0);
        oneHundredth.setMaxValue(9);
        RelativeLayout.LayoutParams lsdParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lsdParams.addRule(RelativeLayout.RIGHT_OF, oneTenth.getId());
        oneHundredth.setLayoutParams(lsdParams);

        percentagePicker.addView(hundred);
        percentagePicker.addView(ten);
        percentagePicker.addView(one);
        percentagePicker.addView(dot);
        percentagePicker.addView(oneTenth);
        percentagePicker.addView(oneHundredth);

        builder.setView(percentagePicker);
        return builder.create();
    }

    public double getTopUp(){
        return hundred.getValue() * 100 + ten.getValue() * 10 + one.getValue() +
                oneTenth.getValue() * 0.1 + oneHundredth.getValue() * 0.01;
    }
}
