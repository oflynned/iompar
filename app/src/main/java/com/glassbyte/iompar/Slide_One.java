package com.glassbyte.iompar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by ed on 19/12/15.
 */
public class Slide_One extends Fragment {

    Globals globals;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView title, description, languageOption;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.slide_one, container, false);
        Switch irishSwitch = (Switch) view.findViewById(R.id.switch_pref_irish);

        globals = new Globals(getActivity());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isIrish = sharedPreferences.getBoolean(getResources()
                .getString(R.string.pref_key_irish), false);
        if(isIrish)
            irishSwitch.setChecked(true);
        else
            irishSwitch.setChecked(false);

        title = (TextView) view.findViewById(R.id.slide_one_title);
        description = (TextView) view.findViewById(R.id.slide_one_desc);
        languageOption = (TextView) view.findViewById(R.id.irish_pref_text);

        globals.setIrish(sharedPreferences.getBoolean(getResources()
                .getString(R.string.pref_key_irish), false), getResources());
        title.invalidate();
        description.invalidate();
        languageOption.invalidate();

        irishSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sharedPreferences.edit();
                editor.putBoolean(getResources().getString(R.string.pref_key_irish), isChecked);
                editor.apply();

                globals.setIrish(sharedPreferences.getBoolean(getResources()
                        .getString(R.string.pref_key_irish), false), getResources());

                title.invalidate();
                title.setText(getString(R.string.slide_one_welcome));
                description.invalidate();
                description.setText(getString(R.string.slide_one_description));
                languageOption.invalidate();
                languageOption.setText(getString(R.string.slide_one_irish_option));
            }
        });

        return view;
    }
}
