package com.syzible.iompar;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ed on 29/10/15.
 */
public class Remind extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_remind_me, null);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.insertRecord(
                Database.LeapLogin.TABLE_NAME,
                null, null, null, null, null, null, 0, null, null, 0, 0, 0, false,
                "12345 12345 1234", "ed@example.com", "test1");
        databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);

        return view;
    }
}
