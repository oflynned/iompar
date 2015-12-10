package com.syzible.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by ed on 29/10/15.
 */
public class Remind extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_remind_me, null);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.clearTable(Database.LeapLogin.TABLE_NAME);
        databaseHelper.insertRecord(
                Database.LeapLogin.TABLE_NAME,
                null, null, null, null, null, null, 0, null, null, 0, 0, 0, false,
                Globals.USER_LEAP_NUMBER, Globals.USER_NAME, Globals.USER_EMAIL, Globals.USER_PASS, true);
        databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);

        return view;
    }
}
