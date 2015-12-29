package com.glassbyte.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ed on 19/12/15.
 */
public class Slide_Two extends Fragment {

    public EditText nameField;
    public TextView title, desc;
    public Spinner fareSelection;
    private boolean notified;
    private String fareType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_two, container, false);

        title = (TextView) view.findViewById(R.id.slide_two_title);
        desc = (TextView) view.findViewById(R.id.slide_two_desc);
        nameField = (EditText) view.findViewById(R.id.intro_name);
        fareSelection = (Spinner) view.findViewById(R.id.fare_selection);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.fare_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fareSelection.setAdapter(adapter);
        fareSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(fareSelection.getItemAtPosition(position).toString().equals(getString(R.string.student))){
                    if(!isNotified()) {
                        Toast.makeText(getContext(),
                                "Please ensure you have a student Leap card in order to avail of student prices.",
                                Toast.LENGTH_LONG).show();
                        setNotified(true);
                    }
                }

                switch (position){
                    case 0:
                        setFareType(getString(R.string.adult));
                        break;
                    case 1:
                        setFareType(getString(R.string.student));
                        break;
                    case 2:
                        setFareType(getString(R.string.child));
                        break;
                    case 3:
                        setFareType(getString(R.string.other));
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        title.setText(getString(R.string.slide_two_title));
        desc.setText(getString(R.string.slide_two_description));
        nameField.setHint(getString(R.string.slide_two_hint));

        title.invalidate();
        desc.invalidate();
        nameField.invalidate();

        return view;
    }

    public String getNameField(){return nameField.getText().toString();}
    public void setNotified(boolean notified){this.notified=notified;}
    public boolean isNotified(){return notified;}
    public void setFareType(String fareType){this.fareType=fareType;}
    public String getFareType(){return fareType;}
}
