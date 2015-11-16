package com.syzible.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ed on 29/10/15.
 */
public class AddLeapCard extends Fragment {

    Sync sync = new Sync();

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_fare, null);

        sync.leapConnect();

        return view;
    }
}
