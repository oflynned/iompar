package com.syzible.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by ed on 19/12/15.
 */
public class Slide_Two extends Fragment {

    private EditText nameField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_two, container, false);

        nameField = (EditText) view.findViewById(R.id.intro_name);

        return view;
    }

    public String getNameField(){return nameField.getText().toString();}
    public boolean isEmpty(){
        if(!getNameField().equals("")){
            return true;
        }
        return false;
    }
}
