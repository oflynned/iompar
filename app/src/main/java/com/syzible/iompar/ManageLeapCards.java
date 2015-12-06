package com.syzible.iompar;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;

import java.util.ArrayList;

/**
 * Created by ed on 29/10/15.
 */
public class ManageLeapCards extends Fragment {

    TableLayout tableLayout;
    ArrayList<CheckBox> checkBoxes;
    View view;

    FilterMenu filterMenu;
    FilterMenuLayout filterMenuLayout;
    DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_manage_leap, null);

        tableLayout = (TableLayout) view.findViewById(R.id.leap_list_table);
        checkBoxes = new ArrayList<>();

        databaseHelper = new DatabaseHelper(getContext());

        databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
        populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);

        filterMenuLayout = (FilterMenuLayout) view.findViewById(R.id.filter_menu);
        filterMenu = new FilterMenu.Builder(getActivity())
                .addItem(R.drawable.ic_action_add)
                .addItem(R.drawable.ic_action_clock)
                .addItem(R.drawable.ic_action_io)
                .addItem(R.drawable.ic_action_location_2)
                .attach(filterMenuLayout)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, final int position) {
                        switch (position) {
                            case 0:
                                final AddLeapCard addLeapCard = new AddLeapCard();
                                addLeapCard.show(ManageLeapCards.this.getFragmentManager(), "addLeapCard");
                                addLeapCard.setAddLeapDialogListener(new AddLeapCard.setAddLeapListener() {
                                    @Override
                                    public void onDoneClick(DialogFragment dialogFragment) {
                                        //inserts entered data and set active card to FALSE
                                        //as it may not be in use
                                        //set card to true from handler on custom table view in parent
                                        databaseHelper.insertRecord(
                                                Database.LeapLogin.TABLE_NAME,
                                                null, null, null, null, null, null, 0, null, null, 0, 0, 0, false,
                                                addLeapCard.getNumberField(), addLeapCard.getUsernameField(),
                                                addLeapCard.getEmailField(), addLeapCard.getPasswordField(), false);
                                        databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
                                        populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                    }
                                });
                                break;
                            case 1:
                                databaseHelper.clearTable(Database.LeapLogin.TABLE_NAME);
                                databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
                                populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                        }
                    }

                    @Override
                    public void onMenuCollapse() {

                    }

                    @Override
                    public void onMenuExpand() {

                    }
                })
                .build();

        return view;
    }

    private void populateTable(String query){
        SQLiteDatabase readDb = databaseHelper.getReadableDatabase();

        final int currNumRows = tableLayout.getChildCount();
        if (currNumRows > 1)
            tableLayout.removeViewsInLayout(1, currNumRows - 1);

        final Cursor cursor = readDb.rawQuery(query, null);
        int numRows = cursor.getCount();
        System.out.println("Row count " + numRows);
        cursor.moveToFirst();

        for (int i = 0; i < numRows; i++) {
            final TableRow tableRow = new TableRow(getContext());
            TableRow.LayoutParams tableRowParams =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(tableRowParams);

            //assign row via ID instantiation
            final int row = cursor.getInt(DatabaseHelper.COL_LEAP_LOGIN_ID);

            //card #
            final TextView cardNumberField = new TextView(getContext());
            cardNumberField.setText(cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER));
            cardNumberField.setGravity(Gravity.CENTER);

            //username field
            final TextView usernameField = new TextView(getContext());
            usernameField.setText(cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_USER_NAME));
            usernameField.setGravity(Gravity.CENTER);

            //balance - need to work on this as it takes from other table
            final TextView balanceField = new TextView(getContext());
            balanceField.setText("€xxx");
            balanceField.setGravity(Gravity.CENTER);

            if (i % 2 == 0) {
                tableRow.setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.colorPrimary));
                cardNumberField.setTextColor(Color.WHITE);
                usernameField.setTextColor(Color.WHITE);
                balanceField.setTextColor(Color.WHITE);
            }

            final CheckBox checkBox = new CheckBox(getContext());
            checkBox.setId(cursor.getInt(DatabaseHelper.COL_LEAP_LOGIN_ID));
            checkBox.setGravity(Gravity.CENTER);
            checkBoxes.add(checkBox);

            if(cursor.getInt(DatabaseHelper.COL_LEAP_LOGIN_IS_ACTIVE) == 1){
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
                }
            });


            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "id: " + row, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            //add views
            tableRow.addView(cardNumberField);
            tableRow.addView(usernameField);
            tableRow.addView(balanceField);
            tableRow.addView(checkBox);

            tableLayout.addView(tableRow);

            cursor.moveToNext();
        }

        tableLayout.invalidate();
        readDb.close();
        cursor.close();
    }
}
