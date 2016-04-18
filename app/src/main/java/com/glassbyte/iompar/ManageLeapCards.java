package com.glassbyte.iompar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_leap, null);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();

        tableLayout = (TableLayout) view.findViewById(R.id.leap_list_table);
        checkBoxes = new ArrayList<>();

        databaseHelper = new DatabaseHelper(getContext());

        populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);

        filterMenuLayout = (FilterMenuLayout) view.findViewById(R.id.filter_menu);
        filterMenu = new FilterMenu.Builder(getActivity())
                .addItem(R.drawable.ic_add_white_18dp)
                .addItem(R.drawable.ic_clear_white_18dp)
                .addItem(R.drawable.ic_input_white_18dp)
                .addItem(R.drawable.ic_autorenew_white_18dp)
                .attach(filterMenuLayout)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, final int position) {
                        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
                        final Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
                        switch (position) {
                            //open dialog to add leap to table
                            case 0:

                                if (cursor.getCount() == 0) {
                                    final AddLeapCard addLeapCard = new AddLeapCard();
                                    addLeapCard.show(ManageLeapCards.this.getFragmentManager(), "addLeapCard");
                                    addLeapCard.setAddLeapDialogListener(new AddLeapCard.setAddLeapListener() {
                                        @Override
                                        public void onDoneClick(DialogFragment dialogFragment) {
                                            if (addLeapCard.getNumberField().matches("") &&
                                                    addLeapCard.getUsernameField().matches("") &&
                                                    addLeapCard.getEmailField().matches("") &&
                                                    addLeapCard.getPasswordField().matches("")) {
                                                Toast.makeText(getActivity(), R.string.complete_all_fields, Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (addLeapCard.getNumberField().matches("")) {
                                                    Toast.makeText(getActivity(), R.string.input_leap_number, Toast.LENGTH_SHORT).show();
                                                }
                                                if (addLeapCard.getUsernameField().matches("")) {
                                                    Toast.makeText(getActivity(), R.string.input_username, Toast.LENGTH_SHORT).show();
                                                }
                                                if (addLeapCard.getEmailField().matches("")) {
                                                    Toast.makeText(getActivity(), R.string.input_email, Toast.LENGTH_SHORT).show();
                                                }
                                                if (addLeapCard.getPasswordField().matches("")) {
                                                    Toast.makeText(getActivity(), R.string.input_password, Toast.LENGTH_SHORT).show();
                                                }
                                                if (!addLeapCard.getNumberField().matches("") &&
                                                        !addLeapCard.getUsernameField().matches("") &&
                                                        !addLeapCard.getEmailField().matches("") &&
                                                        !addLeapCard.getPasswordField().matches("")) {
                                                    databaseHelper.insertRecord(
                                                            Database.LeapLogin.TABLE_NAME,
                                                            null, null, null, null, null, null, 0, 0, 0, 0, 0, false,
                                                            addLeapCard.getUsernameField(), addLeapCard.getNumberField(),
                                                            addLeapCard.getEmailField(), addLeapCard.getPasswordField(), false);
                                                    databaseHelper.modifyActive(Database.LeapLogin.TABLE_NAME, Database.LeapLogin.IS_ACTIVE,
                                                            Database.LeapLogin.ID, 0, true);

                                                    populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                                    databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
                                                    Toast.makeText(getContext(), R.string.leap_added_successfully, Toast.LENGTH_SHORT).show();

                                                    AsynchronousLeapChecking asynchronousLeapChecking = new AsynchronousLeapChecking();
                                                    asynchronousLeapChecking.execute();

                                                }
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), R.string.only_one_leap_warning, Toast.LENGTH_LONG).show();
                                }
                                cursor.close();
                                sqLiteDatabase.close();
                                break;
                            //clear table
                            case 1:
                                new AlertDialog.Builder(getContext())
                                        .setTitle(getString(R.string.clear_leap_cards))
                                        .setMessage(getString(R.string.confirm_remove_all_cards))
                                        .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                databaseHelper.clearTable(Database.LeapLogin.TABLE_NAME);
                                                databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
                                                populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                                Toast.makeText(getContext(), R.string.cards_cleared_successfully, Toast.LENGTH_SHORT).show();
                                                editor.putString(getString(R.string.pref_key_curr_synced_balance), "unsynced").apply();
                                                editor.putString(getString(R.string.pref_key_last_synced_balance), "unsynced").apply();
                                                editor.putString(getString(R.string.pref_key_current_balance), "unsynced").apply();
                                                editor.putBoolean(getString(R.string.pref_key_first_sync), false).apply();
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                                cursor.close();
                                sqLiteDatabase.close();
                                break;
                            //breakdown of costs
                            case 2:
                                if (cursor.getCount() > 0) {
                                    AsynchronousLeapBalance asynchronousLeapBalance = new AsynchronousLeapBalance(getContext());
                                    asynchronousLeapBalance.execute();
                                } else {
                                    Toast.makeText(getContext(), getString(R.string.no_active_leap), Toast.LENGTH_LONG).show();
                                }
                                cursor.close();
                                sqLiteDatabase.close();
                                break;
                            //force sync
                            case 3:
                                if (cursor.getCount() > 0) {
                                    AsynchronousLeapChecking asynchronousLeapChecking = new AsynchronousLeapChecking();
                                    asynchronousLeapChecking.execute();
                                } else {
                                    Toast.makeText(getContext(), getString(R.string.no_active_leap), Toast.LENGTH_LONG).show();
                                }
                                cursor.close();
                                sqLiteDatabase.close();
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
        if (numRows > 0) {
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
                            for (CheckBox cb : checkBoxes) {
                                cb.setChecked(false);
                            }
                            for (int j = 0; j <= numRows; j++) {
                                databaseHelper.modifyActive(Database.LeapLogin.TABLE_NAME,
                                        Database.LeapLogin.IS_ACTIVE, Database.LeapLogin.ID, j, false);
                            }
                            databaseHelper.modifyActive(Database.LeapLogin.TABLE_NAME,
                                    Database.LeapLogin.IS_ACTIVE, Database.LeapLogin.ID, row, isChecked);
                            checkBox.setChecked(true);
                        } else {
                            for (CheckBox cb : checkBoxes) {
                                cb.setChecked(false);
                            }
                            for (int j = 0; j <= numRows; j++) {
                                databaseHelper.modifyActive(Database.LeapLogin.TABLE_NAME,
                                        Database.LeapLogin.IS_ACTIVE, Database.LeapLogin.ID, j, false);
                                checkBox.setChecked(false);
                            }
                        }

                        MainActivity.drawer.invalidate();
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

                final TextView balanceField = new TextView(getContext());
                TableRow.LayoutParams balanceParams =
                        new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                MainActivity.getCurrentBalance(databaseHelper, sharedPreferences, getContext());
                if(!sharedPreferences.getString(getString(R.string.pref_key_current_balance), "").equals("")){
                    balanceField.setText(sharedPreferences.getString(getString(R.string.pref_key_current_balance), ""));
                } else {
                    balanceField.setText(sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), ""));
                }
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

                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.what_would_you_like_to_do))
                                .setMessage(getString(R.string.edit_current_leap) + MainActivity.getSelectedLeapNumber(databaseHelper, row) + getString(R.string.enter_top_up_amount))
                                .setPositiveButton(getString(R.string.topup), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final TopUpDialog topUpDialog = new TopUpDialog();
                                        topUpDialog.show(getActivity().getFragmentManager(), "TopUpListener");
                                        topUpDialog.setSetTopUpDialogListener(new TopUpDialog.SetTopUpListener() {
                                            @Override
                                            public void onDoneClick(android.app.DialogFragment dialog) {
                                                SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
                                                Cursor cursor = sqLiteDatabase.rawQuery(
                                                        DatabaseHelper.SELECT_ALL_LEAP_LOGIN, null);
                                                if (cursor.getCount() > 0) {
                                                    cursor.moveToFirst();
                                                    cursor.moveToPosition(row - 1);
                                                    Toast.makeText(getActivity(), getString(R.string.topped_up_by) + Fares.formatDecimals(String.valueOf(topUpDialog.getTopUp())) +
                                                                    getString(R.string.on) + cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER),
                                                            Toast.LENGTH_LONG).show();
                                                    if (cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_IS_ACTIVE).equals("0")) {
                                                        databaseHelper.modifyActive(Database.LeapLogin.TABLE_NAME, Database.LeapLogin.IS_ACTIVE,
                                                                Database.LeapLogin.ID, row, true);
                                                    }
                                                    System.out.println("TOPUP " + topUpDialog.getTopUp());
                                                    databaseHelper.printTableContents(Database.Expenditures.TABLE_NAME);
                                                    updateLocalBalance(databaseHelper, "add topup", topUpDialog.getTopUp(), 0);

                                                    System.out.println(sharedPreferences.getString(getString(R.string.pref_key_current_balance), ""));
                                                    System.out.println(sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), ""));

                                                    tableLayout.invalidate();
                                                    populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                                    Toast.makeText(getActivity(), getString(R.string.bal_updated_successfully) +
                                                                    " (" + cursor.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER) + ")",
                                                            Toast.LENGTH_SHORT).show();

                                                }
                                                MainActivity.getCurrentBalance(databaseHelper, sharedPreferences, getContext());
                                                databaseHelper.close();
                                                cursor.close();
                                                sqLiteDatabase.close();
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //create a dialog here to modify leap details for given row
                                        final EditLeapCard editLeapCard = new EditLeapCard();

                                        setData(row, DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER);
                                        editLeapCard.setNumberField(getData());

                                        setData(row, DatabaseHelper.COL_LEAP_LOGIN_USER_NAME);
                                        editLeapCard.setUsernameField(getData());

                                        setData(row, DatabaseHelper.COL_LEAP_LOGIN_EMAIL);
                                        editLeapCard.setEmailField(getData());

                                        setData(row, DatabaseHelper.COL_LEAP_LOGIN_PASSWORD);
                                        editLeapCard.setPasswordField(getData());

                                        editLeapCard.show(ManageLeapCards.this.getFragmentManager(), "setEditDialogListener");
                                        editLeapCard.setEditLeapDialogListener(new EditLeapCard.setEditLeapListener() {
                                            @Override
                                            public void onDoneClick(DialogFragment dialogFragment) {

                                                if (editLeapCard.getNumberField().matches("") &&
                                                        editLeapCard.getUsernameField().matches("") &&
                                                        editLeapCard.getEmailField().matches("") &&
                                                        editLeapCard.getPasswordField().matches("")) {
                                                    Toast.makeText(getActivity(), R.string.complete_all_fields,
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    if (editLeapCard.getNumberField().matches("")) {
                                                        Toast.makeText(getActivity(), R.string.input_leap_number,
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                    if (editLeapCard.getUsernameField().matches("")) {
                                                        Toast.makeText(getActivity(), R.string.input_username,
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                    if (editLeapCard.getEmailField().matches("")) {
                                                        Toast.makeText(getActivity(), R.string.input_email,
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                    if (editLeapCard.getPasswordField().matches("")) {
                                                        Toast.makeText(getActivity(), R.string.input_password,
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                    if (!editLeapCard.getNumberField().matches("")
                                                            && !editLeapCard.getUsernameField().matches("")
                                                            && !editLeapCard.getEmailField().matches("")
                                                            && !editLeapCard.getPasswordField().matches("")) {

                                                        databaseHelper.modifyLeapCard(
                                                                row,
                                                                editLeapCard.getNumberField(),
                                                                editLeapCard.getUsernameField(),
                                                                editLeapCard.getEmailField(),
                                                                editLeapCard.getPasswordField());
                                                        databaseHelper.printTableContents(Database.LeapLogin.TABLE_NAME);
                                                        tableLayout.invalidate();
                                                        populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                                        Toast.makeText(getActivity(), R.string.leap_modified_successfully,
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                })
                                .create()
                                .show();
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
        } else {
            Toast.makeText(getContext(), R.string.add_leap_cheaper_fares,
                    Toast.LENGTH_LONG).show();
        }

        tableLayout.invalidate();
        readDb.close();
        cursor.close();
    }

    /**
     * given the data set in the row/col mix, the value is given in string format
     *
     * @return the value for the field as a string
     */
    public String getData() {
        return data;
    }

    /**
     * retrieves the appropriate data for the edit record dialog box
     * in order to populate the given fields with the appropriate values
     * as a generic function
     *
     * @param row the given row to be accessed with respect to ID
     *            which is always the row number + 1
     * @param col the given column to be accessed where the column
     *            corresponds to the queries in the database helper class
     */
    public void setData(int row, int col) {
        SQLiteDatabase readDb = databaseHelper.getReadableDatabase();
        Cursor cursor = readDb.rawQuery(DatabaseHelper.SELECT_ALL_LEAP_LOGIN, null);
        cursor.moveToFirst();
        cursor.move(row - 1);
        this.data = cursor.getString(col);
        readDb.close();
        cursor.close();
    }

    public static void updateLocalBalance(DatabaseHelper databaseHelper, String updateType, double amount, int id) {
        switch (updateType) {
            case "add topup":
                System.out.println("adding topup");
                databaseHelper.insertExpenditure("topup", MainActivity.getActiveLeapNumber(databaseHelper), Fares.formatDecimals(amount));
                break;
            case "add cash":
                databaseHelper.insertExpenditure("cash", MainActivity.getActiveLeapNumber(databaseHelper), Fares.formatDecimals(amount));
                break;
            case "add Leap":
                databaseHelper.insertExpenditure("Leap", MainActivity.getActiveLeapNumber(databaseHelper), Fares.formatDecimals(amount));
                break;
            case "add amend":
                databaseHelper.insertExpenditure("amend", MainActivity.getActiveLeapNumber(databaseHelper), Fares.formatDecimals(amount));
                break;
            case "remove":
                databaseHelper.removeRecord(Database.Expenditures.TABLE_NAME, Database.Expenditures.ID, id);
                break;
        }
    }

    public class AsynchronousLeapBalance extends AsyncTask<Void, Void, Void> {

        Leap leap;
        Context context;
        boolean isTimeout = false;

        public AsynchronousLeapBalance(Context context) {
            this.context = context;
            leap = new Leap(context);
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(context, R.string.retrieving_leap_data, Toast.LENGTH_LONG).show();
            leap.retrieveLeapcardrBalance();
        }

        @Override
        protected Void doInBackground(Void... params) {
            long timeout = 0;
            while (!leap.isSynced()) {
                try {
                    System.out.println("sleeping");
                    timeout += Globals.ONE_SECOND;
                    Thread.sleep(Globals.ONE_SECOND);

                    if (timeout > Globals.SIXTY_SECONDS * 2) {
                        leap.setSynced(true);
                        isTimeout = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!isTimeout) {
                if (leap.isSynced()) {
                    System.out.println("synced");
                    SQLiteDatabase readDB = databaseHelper.getReadableDatabase();
                    Cursor cursorNumber = readDB.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
                    if (cursorNumber.getCount() > 0) {
                        cursorNumber.moveToFirst();
                        if (leap.isSynced()) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle(context.getString(R.string.online_bal_leap_dialog))
                                    .setMessage(context.getString(R.string.online_bal_reported) + leap.getBalance()
                                            + context.getString(R.string.for_connective_report) + cursorNumber.getString(DatabaseHelper.COL_LEAP_LOGIN_CARD_NUMBER) + ".\n\n" +
                                            context.getString(R.string.bal_may_not_be_accurate))
                                    .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                    }
                    cursorNumber.close();
                    readDB.close();
                }
            } else {
                isTimeout = false;
                Toast.makeText(context, R.string.connection_timed_out, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Deprecated
    public class AsynchronousLeapChecking extends AsyncTask<Void, Void, Void> {

        Leap leap;
        boolean isIncomplete = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            leap = new Leap(getActivity());
            leap.retrieveLeapcardrBalance();
        }

        @Override
        protected Void doInBackground(Void... params) {
            long timer = 0;
            while (!leap.isSynced()) {
                System.out.println("NOT synced and has slept " + (timer / 1000) + "/60");
                try {
                    timer += Globals.ONE_SECOND;
                    Thread.currentThread();
                    Thread.sleep(Globals.ONE_SECOND);
                    if (timer > Globals.SIXTY_SECONDS) {
                        isIncomplete = true;
                        leap.setSynced(true);
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!isIncomplete) {
                if (leap.isSynced()) {
                    editor = sharedPreferences.edit();
                    System.out.println("is synced");
                    SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
                    Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
                    if (cursor.getCount() == 0) {
                        editor.putBoolean(getString(R.string.pref_key_first_sync), false).apply();
                        editor.putString(getString(R.string.pref_key_last_synced_balance), "").apply();
                    }
                    sqLiteDatabase.close();
                    cursor.close();

                    if (!sharedPreferences.getBoolean(getString(R.string.pref_key_first_sync), false)) {
                        System.out.println("first time run - sync balances via dialog");
                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.first_time_sync))
                                .setMessage(getString(R.string.first_sync_confirmation_dialog) + leap.getBalance() + getString(R.string.first_sync_dialog_part_two))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getContext(), getString(R.string.reported_balance) + leap.getBalance(), Toast.LENGTH_LONG).show();
                                        editor.putBoolean(getString(R.string.pref_key_first_sync), true).apply();
                                        editor.putString(getString(R.string.pref_key_last_synced_balance), leap.getBalance()).apply();
                                        populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                    }
                                })
                                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //amend this value as it's not correct
                                        final CorrectBalanceDialog correctBalanceDialog = new CorrectBalanceDialog();
                                        correctBalanceDialog.show(getActivity().getFragmentManager(), "AmendBalance");
                                        correctBalanceDialog.setSetBalanceDialogListener(new CorrectBalanceDialog.SetBalanceListener() {
                                            @Override
                                            public void onDoneClick(android.app.DialogFragment dialog) {
                                                if (correctBalanceDialog.getBalance().contains("-")) {
                                                    Toast.makeText(getContext(), getString(R.string.amended_balance_neg) + correctBalanceDialog.getBalance().replace("-", ""), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), getString(R.string.amended_balance_pos) + correctBalanceDialog.getBalance(), Toast.LENGTH_LONG).show();
                                                }

                                                String balance;
                                                if (correctBalanceDialog.getBalance().contains("-")) {
                                                    balance = "-€" + correctBalanceDialog.getBalance().replace("-", "");
                                                } else {
                                                    balance = "€" + correctBalanceDialog.getBalance();
                                                }

                                                //STORE BALANCE IN TABLE
                                                if (!sharedPreferences.getBoolean(getString(R.string.pref_key_first_sync), false)) {
                                                    editor.putString(getString(R.string.pref_key_last_synced_balance), balance).apply();
                                                    System.out.println("balance amended: " + sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), ""));
                                                    populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                                    //prevent initial sync on next iterations
                                                    editor.putBoolean(getString(R.string.pref_key_first_sync), true).apply();
                                                } else {
                                                    editor.putString(getString(R.string.pref_key_last_synced_balance), balance).apply();
                                                    System.out.println("balance amended: " + sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), ""));
                                                    populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                                }
                                            }
                                        });
                                    }
                                })
                                .show();
                    } else {
                        if (cursor.getCount() > 0) {
                            MainActivity.getCurrentBalance(databaseHelper, sharedPreferences, getContext());
                            String oldSyncCumulative = sharedPreferences.getString(getString(R.string.pref_key_current_balance), "");
                            if (oldSyncCumulative.equals("")) {
                                oldSyncCumulative = sharedPreferences.getString(getString(R.string.pref_key_last_synced_balance), "");
                            }
                            if(oldSyncCumulative.equals("")){
                                oldSyncCumulative = "0";
                            }
                            final String oldSync = oldSyncCumulative;
                            final String newSync = leap.getBalance();

                            if (oldSync.equals(newSync)) {
                                //do nothing -- balances coincide so we don't have to modify anything
                            } else {
                                System.out.println("balances are different");
                                //problem - balances aren't the same, ask the user which to use and allow for input if online is wrong
                                new AlertDialog.Builder(getContext())
                                        .setTitle(getString(R.string.differing_balances))
                                        .setMessage(getString(R.string.online_bal_leap_reported) + newSync + getString(R.string.local_bal_is) +
                                                oldSync + getString(R.string.is_leap_correct))
                                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getContext(), getString(R.string.bal_updated_successfully) + leap.getBalance() + ")", Toast.LENGTH_LONG).show();
                                                //leap online is correct, get difference between two values to account as an amend
                                                double balanceAmend = Double.parseDouble(newSync.replace("€", "")) - Double.parseDouble(oldSync.replace("€", ""));
                                                databaseHelper.insertExpenditure("amend", MainActivity.getActiveLeapNumber(databaseHelper), Fares.formatDecimals(balanceAmend));
                                                populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //amend this value as it's not correct
                                                final CorrectBalanceDialog correctBalanceDialog = new CorrectBalanceDialog();
                                                correctBalanceDialog.show(getActivity().getFragmentManager(), "AmendBalance");
                                                correctBalanceDialog.setSetBalanceDialogListener(new CorrectBalanceDialog.SetBalanceListener() {
                                                    @Override
                                                    public void onDoneClick(android.app.DialogFragment dialog) {
                                                        if (correctBalanceDialog.getBalance().contains("-")) {
                                                            Toast.makeText(getContext(), getString(R.string.amended_balance_neg) + correctBalanceDialog.getBalance().replace("-", ""), Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(getContext(), getString(R.string.amended_balance_pos) + correctBalanceDialog.getBalance(), Toast.LENGTH_LONG).show();
                                                        }

                                                        String balance;
                                                        if (correctBalanceDialog.getBalance().contains("-")) {
                                                            balance = "-€" + correctBalanceDialog.getBalance().replace("-", "");
                                                        } else {
                                                            balance = "€" + correctBalanceDialog.getBalance();
                                                        }
                                                        editor.putString(getString(R.string.pref_key_current_balance), balance).apply();
                                                        System.out.println("balance amended: " + sharedPreferences.getString(getString(R.string.pref_key_curr_synced_balance), ""));

                                                        double balanceAmend = Double.parseDouble(correctBalanceDialog.getBalance().replace("€", "")) - Double.parseDouble(oldSync.replace("€", ""));
                                                        databaseHelper.insertExpenditure("amend", MainActivity.getActiveLeapNumber(databaseHelper), Fares.formatDecimals(balanceAmend));
                                                        tableLayout.invalidate();
                                                        populateTable(DatabaseHelper.SELECT_ALL_LEAP_LOGIN);
                                                    }
                                                });
                                            }
                                        })
                                        .show();
                            }
                        }
                        cursor.close();
                        sqLiteDatabase.close();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.unable_to_sync_bal, Toast.LENGTH_LONG).show();
                    isIncomplete = false;
                }
            }
        }
    }
}
