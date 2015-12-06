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
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_leap, null);

        tableLayout = (TableLayout) view.findViewById(R.id.leap_list_table);
        checkBoxes = new ArrayList<>();

        databaseHelper = new DatabaseHelper(getContext());

        databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
        populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);

        filterMenuLayout = (FilterMenuLayout) view.findViewById(R.id.filter_menu);
        filterMenu = new FilterMenu.Builder(getActivity())
                .addItem(R.drawable.ic_add_white_18dp)
                .addItem(R.drawable.ic_clear_white_18dp)
                .addItem(R.drawable.ic_content_paste_white_18dp)
                .attach(filterMenuLayout)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, final int position) {
                        switch (position) {
                            //open dialog to add leap to table
                            case 0:
                                final AddLeapCard addLeapCard = new AddLeapCard();
                                addLeapCard.show(ManageLeapCards.this.getFragmentManager(), "addLeapCard");
                                addLeapCard.setAddLeapDialogListener(new AddLeapCard.setAddLeapListener() {
                                    @Override
                                    public void onDoneClick(DialogFragment dialogFragment) {
                                        databaseHelper.insertRecord(
                                                Database.LeapLogin.TABLE_NAME,
                                                null, null, null, null, null, null, 0, null, null, 0, 0, 0, false,
                                                addLeapCard.getNumberField(), addLeapCard.getUsernameField(),
                                                addLeapCard.getEmailField(), addLeapCard.getPasswordField(), true);
                                        databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
                                        populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                        Toast.makeText(getContext(), "Leap card added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            //clear table
                            case 1:
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Clear Leap Cards")
                                        .setMessage("Are you sure you want to remove all cards?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                databaseHelper.clearTable(Database.LeapLogin.TABLE_NAME);
                                                databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
                                                populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                                Toast.makeText(getContext(), "Cards all cleared successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                                break;
                            //breakdown of costs
                            case 2:
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Active Leap Card History")
                                        .setMessage("Expenditures here")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
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

    private void populateTable(String query) {
        SQLiteDatabase readDb = databaseHelper.getReadableDatabase();

        final int currNumRows = tableLayout.getChildCount();
        if (currNumRows > 1)
            tableLayout.removeViewsInLayout(1, currNumRows - 1);

        final Cursor cursor = readDb.rawQuery(query, null);
        final int numRows = cursor.getCount();
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

            //currently active leap card
            final CheckBox checkBox = new CheckBox(getContext());
            checkBox.setId(cursor.getInt(DatabaseHelper.COL_LEAP_LOGIN_ID));
            TableRow.LayoutParams checkboxParams =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            checkboxParams.gravity = Gravity.CENTER;
            checkBox.setLayoutParams(checkboxParams);
            checkBoxes.add(checkBox);

            if (cursor.getInt(DatabaseHelper.COL_LEAP_LOGIN_IS_ACTIVE) == 1) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkBox.isChecked()) {
                        for(CheckBox cb : checkBoxes){
                            cb.setChecked(false);
                        }
                        for (int j = 0; j <= numRows; j++) {
                            databaseHelper.modifyActive(Database.LeapLogin.TABLE_NAME, Database.LeapLogin.IS_ACTIVE, Database.LeapLogin.ID, j, false);
                        }
                        databaseHelper.modifyActive(Database.LeapLogin.TABLE_NAME, Database.LeapLogin.IS_ACTIVE, Database.LeapLogin.ID, row, isChecked);
                        checkBox.setChecked(true);
                    } else {
                        for(CheckBox cb : checkBoxes){
                            cb.setChecked(false);
                        }
                        for (int j = 0; j <= numRows; j++) {
                            databaseHelper.modifyActive(Database.LeapLogin.TABLE_NAME, Database.LeapLogin.IS_ACTIVE, Database.LeapLogin.ID, j, false);
                            checkBox.setChecked(false);
                        }
                    }
                    tableLayout.invalidate();
                    databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
                }
            });

            //card #
            final TextView cardNumberField = new TextView(getContext());
            cardNumberField.setText(cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER));
            TableRow.LayoutParams cardNumberParams =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            cardNumberParams.gravity = Gravity.CENTER;
            cardNumberField.setLayoutParams(cardNumberParams);

            //username field
            final TextView usernameField = new TextView(getContext());
            usernameField.setText(cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_USER_NAME));
            TableRow.LayoutParams usernameParams =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            usernameParams.gravity = Gravity.CENTER;
            usernameField.setLayoutParams(usernameParams);

            //balance - need to work on this as it takes from other table
            final TextView balanceField = new TextView(getContext());
            balanceField.setText("€xxx");
            TableRow.LayoutParams balanceParams =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            balanceParams.gravity = Gravity.CENTER;
            balanceField.setLayoutParams(balanceParams);

            if (i % 2 == 0) {
                tableRow.setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.colorPrimary));
                cardNumberField.setTextColor(Color.WHITE);
                usernameField.setTextColor(Color.WHITE);
                balanceField.setTextColor(Color.WHITE);
            }

            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getContext(), "id: " + row, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            //add views
            tableRow.addView(checkBox);
            tableRow.addView(cardNumberField);
            tableRow.addView(usernameField);
            tableRow.addView(balanceField);

            tableLayout.addView(tableRow);

            cursor.moveToNext();
        }

        tableLayout.invalidate();
        readDb.close();
        cursor.close();
    }
}
