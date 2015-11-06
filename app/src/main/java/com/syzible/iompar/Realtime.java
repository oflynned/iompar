package com.syzible.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ed on 29/10/15.
 */
public class Realtime extends Fragment {

    View view;

    //xml
    Button stStephensGreenBtn;
    Button theGallopsBtn;
    TextView reportedTimes;

    Sync sync = new Sync(getContext());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_realtime, null);

        stStephensGreenBtn = (Button) view.findViewById(R.id.stephensGreen);
        theGallopsBtn = (Button) view.findViewById(R.id.theGallops);
        reportedTimes = (TextView) view.findViewById(R.id.reportedTimes);

        stStephensGreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String result = sync.requestUpdate(Globals.Type.luas,
                            Globals.LineDirection.stephens_green_to_brides_glen,
                            "St. Stephen's Green",
                            "Charlemont");
                    reportedTimes.setText(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        theGallopsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String result = sync.requestUpdate(Globals.Type.luas,
                            Globals.LineDirection.brides_glen_to_stephens_green,
                            "The Gallops",
                            "St. Stephen's Green");
                    reportedTimes.setText(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        return view;
    }
}
