package com.glassbyte.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by ed on 19/12/15.
 */
public class Slide_Three extends Fragment {

    TextView paymentInformation;
    Switch cash, leap;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.slide_three, container, false);
        cash = (Switch) view.findViewById(R.id.switch_pref_cash);
        leap = (Switch) view.findViewById(R.id.switch_pref_leap);
        paymentInformation = (TextView) view.findViewById(R.id.payment_desc);

        leap.setChecked(true);
        paymentInformation.setText(getString(R.string.slide_three_desc_cash));

        cash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    leap.setChecked(false);
                    paymentInformation.setText(getString(R.string.slide_three_desc_leap));
                } else {
                    leap.setChecked(true);
                    paymentInformation.setText(getString(R.string.slide_three_desc_cash));
                }
            }
        });

        leap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cash.setChecked(false);
                    paymentInformation.setText(getString(R.string.slide_three_desc_cash));
                } else {
                    cash.setChecked(true);
                    paymentInformation.setText(getString(R.string.slide_three_desc_leap));
                }
            }
        });

        return view;
    }
}
