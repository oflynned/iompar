package com.syzible.iompar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Time;

/**
 * Created by ed on 28/10/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private Context context;

    //queries
    public static final String q = "";

    public DatabaseHelper(Context context){
        super(context, Database.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //4 tables:
    //#1: favourites
    public long insertFavourite(int stopId, int stopNumber, String transitType){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();//create instance; id_col = col 0
        contentValues.put(Database.Favourites.STOP_ID, stopId); //col 1
        contentValues.put(Database.Favourites.STOP_NUMBER, stopNumber); //col 2
        contentValues.put(Database.Favourites.TRANSIT_TYPE, transitType); //col 3

        return sqLiteDatabase.insert(Database.Favourites.TABLE_NAME, null, contentValues);
    }

    public void removeFavourite(int stopId){
        SQLiteDatabase sqLiteDatabaseReadable = this.getReadableDatabase();
        SQLiteDatabase sqLiteDatabaseWritable = this.getWritableDatabase();

        String deleteFavouriteFromTable = "DELETE FROM " + Database.Favourites.TABLE_NAME +
                                            " WHERE " + Database.Favourites.STOP_ID +
                                            "=" + stopId;

        sqLiteDatabaseWritable.execSQL(deleteFavouriteFromTable);
    }

    public void updateFavourite(){

    }

    //#2: notifications for regular times
    public long addRegular(int transitId, String transitType, Time time){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();//create instance; id_col = col 0
        contentValues.put(Database.RegularNotifications.TRANSIT_ID, transitId); //col 1
        contentValues.put(Database.RegularNotifications.TRANSIT_TYPE, transitType); //col 2
        contentValues.put(Database.RegularNotifications.TIME, String.valueOf(time)); //col 3

        return sqLiteDatabase.insert(Database.RegularNotifications.TABLE_NAME, null, contentValues);
    }

    public void removeRegular(){

    }

    public void updateRegular(){

    }

    //#3: recorded leap balance
    public long addExpenditure(int leapId, long number, double balance){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();//create instance; id_col = col 0
        contentValues.put(Database.LeapBalance.LEAP_ID, leapId); //col 1
        contentValues.put(Database.LeapBalance.LEAP_NUMBER, number); //col 2
        contentValues.put(Database.LeapBalance.LEAP_BALANCE, balance); //col 3

        return sqLiteDatabase.insert(Database.LeapBalance.TABLE_NAME, null, contentValues);
    }

    public void removeExpenditure(){

    }

    public void updateExpenditure(){

    }

    //#4: information for leap login
    public long addLeap(int leapId, String number, String email, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();//create instance; id_col = col 0
        contentValues.put(Database.LeapLogin.LEAP_ID, leapId); //col 1
        contentValues.put(Database.LeapLogin.LEAP_NUMBER, number); //col 2
        contentValues.put(Database.LeapLogin.USER_EMAIL, email); //col 3
        contentValues.put(Database.LeapLogin.USER_PASSWORD, password); //col 4

        return sqLiteDatabase.insert(Database.LeapLogin.TABLE_NAME, null, contentValues);

    }

    public void removeLeap(){

    }

    public void updateLeap(){

    }
}
