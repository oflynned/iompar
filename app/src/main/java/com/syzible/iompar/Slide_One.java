package com.syzible.iompar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

        globals = new Globals();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        title = (TextView) view.findViewById(R.id.slide_one_title);
        description = (TextView) view.findViewById(R.id.slide_one_desc);
        languageOption = (TextView) view.findViewById(R.id.irish_pref_text);

        globals.setIrish(sharedPreferences.getBoolean(getResources()
                .getString(R.string.pref_key_irish), false), getResources());
        title.invalidate();
        description.invalidate();
        languageOption.invalidate();

        Switch irishSwitch = (Switch) view.findViewById(R.id.switch_pref_irish);
        irishSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sharedPreferences.edit();
                editor.putBoolean(getResources().getString(R.string.pref_key_irish), isChecked);
                editor.apply();

                globals.setIrish(sharedPreferences.getBoolean(getResources()
                        .getString(R.string.pref_key_irish), false), getResources());

                title.invalidate();
                title.getText();
                description.invalidate();
                description.getText();
                languageOption.invalidate();
                languageOption.getText();
            }
        });

        return view;
    }
}
