package com.syzible.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ed on 19/12/15.
 */
public class Slide_Two extends Fragment {

    public EditText nameField;
    public TextView title, desc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_two, container, false);

        title = (TextView) view.findViewById(R.id.slide_two_title);
        desc = (TextView) view.findViewById(R.id.slide_two_desc);
        nameField = (EditText) view.findViewById(R.id.intro_name);

        title.setText(getString(R.string.slide_two_title));
        desc.setText(getString(R.string.slide_two_description));
        nameField.setHint(getString(R.string.slide_two_hint));

        title.invalidate();
        desc.invalidate();
        nameField.invalidate();

        return view;
    }

    public String getNameField(){return nameField.getText().toString();}
    public boolean isEmpty(){
        if(!getNameField().equals("")){
            return false;
        }
        return true;
    }
}
