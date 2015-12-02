package com.syzible.iompar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ed on 28/10/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private Context context;

    //QUERIES
    //favourites
    public static final String CREATE_TABLE_DUBLIN_BUS_FAVOURITES =
            "CREATE TABLE " +
                    Database.DublinBusFavourites.TABLE_NAME + "(" +
                    Database.DublinBusFavourites.ID + " INTEGER PRIMARY KEY," +
                    Database.DublinBusFavourites.STOP_NUMBER + " INTEGER," +
                    Database.DublinBusFavourites.ROUTE + " TEXT," +
                    Database.DublinBusFavourites.FREQUENCY + " INTEGER);";

    public static final String CREATE_TABLE_BUS_EIREANN_FAVOURITES =
            "CREATE TABLE " +
                    Database.BusEireannFavourites.TABLE_NAME + "(" +
                    Database.BusEireannFavourites.ID + " INTEGER PRIMARY KEY," +
                    Database.BusEireannFavourites.STOP_NUMBER + " INTEGER," +
                    Database.BusEireannFavourites.ROUTE + " TEXT," +
                    Database.BusEireannFavourites.DESTINATION + " TEXT," +
                    Database.BusEireannFavourites.FREQUENCY + " INTEGER);";

    public static final String CREATE_TABLE_LUAS_FAVOURITES =
            "CREATE TABLE " +
                    Database.LuasFavourites.TABLE_NAME + "(" +
                    Database.LuasFavourites.ID + " INTEGER PRIMARY KEY," +
                    Database.LuasFavourites.STATION + " TEXT," +
                    Database.LuasFavourites.DIRECTION + " TEXT," +
                    Database.LuasFavourites.FREQUENCY + " INTEGER);";

    public static final String CREATE_TABLE_DART_FAVOURITES =
            "CREATE TABLE " +
                    Database.DartFavourites.TABLE_NAME + "(" +
                    Database.DartFavourites.ID + " INTEGER PRIMARY KEY," +
                    Database.DartFavourites.STATION + " TEXT," +
                    Database.DartFavourites.DIRECTION + " TEXT," +
                    Database.DartFavourites.FREQUENCY + " INTEGER);";

    public static final String CREATE_TABLE_TRAIN_FAVOURITES =
            "CREATE TABLE " +
                    Database.TrainFavourites.TABLE_NAME + "(" +
                    Database.TrainFavourites.ID + " INTEGER PRIMARY KEY," +
                    Database.TrainFavourites.STATION + " TEXT," +
                    Database.TrainFavourites.DIRECTION + " TEXT," +
                    Database.TrainFavourites.FREQUENCY + " INTEGER);";

    public static final String CREATE_TABLE_LEAP_BALANCE =
            "CREATE TABLE " +
                    Database.LeapBalance.TABLE_NAME + "(" +
                    Database.LeapBalance.ID + " INTEGER PRIMARY KEY" +
                    Database.LeapBalance.NUMBER + " INTEGER" +
                    Database.LeapBalance.BALANCE + " REAL);";

    public static final String CREATE_TABLE_LEAP_LOGIN =
            "CREATE TABLE " +
                    Database.LeapLogin.TABLE_NAME + "(" +
                    Database.LeapLogin.ID + " INTEGER PRIMARY KEY," +
                    Database.LeapLogin.CARD_NUMBER + "INTEGER" +
                    Database.LeapLogin.USER_EMAIL + " VARCHAR(320)" +
                    Database.LeapLogin.USER_PASSWORD + " VARCHAR(320));";

    public static final String DELETE_TABLE_DUBLIN_BUS =
            "DROP TABLE IF EXISTS " + Database.DublinBusFavourites.TABLE_NAME + ";";

    public static final String DELETE_TABLE_BUS_EIREANN =
            "DROP TABLE IF EXISTS " + Database.BusEireannFavourites.TABLE_NAME + ";";

    public static final String DELETE_TABLE_LUAS =
            "DROP TABLE IF EXISTS " + Database.LuasFavourites.TABLE_NAME + ";";

    public static final String DELETE_TABLE_DART =
            "DROP TABLE IF EXISTS " + Database.DartFavourites.TABLE_NAME + ";";

    public static final String DELETE_TABLE_TRAIN =
            "DROP TABLE IF EXISTS " + Database.TrainFavourites.TABLE_NAME + ";";

    public static final String DELETE_TABLE_LEAP_BALANCE =
            "DROP TABLE IF EXISTS " + Database.LeapBalance.TABLE_NAME + ";";

    public static final String DELETE_TABLE_LEAP_LOGIN =
            "DROP TABLE IF EXISTS " + Database.LeapLogin.TABLE_NAME + ";";


    public static final String SELECT_ALL_DUBLIN_BUS =
            "SELECT * FROM " + Database.DublinBusFavourites.TABLE_NAME + ";";

    public static final String SELECT_ALL_BUS_EIREANN =
            "SELECT * FROM " + Database.BusEireannFavourites.TABLE_NAME + ";";

    public static final String SELECT_ALL_LUAS =
            "SELECT * FROM " + Database.LuasFavourites.TABLE_NAME + ";";

    public static final String SELECT_ALL_DART =
            "SELECT * FROM " + Database.DartFavourites.TABLE_NAME + ";";

    public static final String SELECT_ALL_TRAIN =
            "SELECT * FROM " + Database.TrainFavourites.TABLE_NAME + ";";

    public static final String SELECT_ALL_LEAP_BALANCE =
            "SELECT * FROM " + Database.LeapBalance.TABLE_NAME + ";";

    public static final String SELECT_ALL_LEAP_LOGIN =
            "SELECT * FROM " + Database.LeapLogin.TABLE_NAME + ";";

    public DatabaseHelper(Context context){
        super(context, Database.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BUS_EIREANN_FAVOURITES);
        db.execSQL(CREATE_TABLE_DART_FAVOURITES);
        db.execSQL(CREATE_TABLE_DUBLIN_BUS_FAVOURITES);
        db.execSQL(CREATE_TABLE_LUAS_FAVOURITES);
        db.execSQL(CREATE_TABLE_TRAIN_FAVOURITES);
        db.execSQL(CREATE_TABLE_LEAP_BALANCE);
        db.execSQL(CREATE_TABLE_LEAP_LOGIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_BUS_EIREANN);
        db.execSQL(DELETE_TABLE_DART);
        db.execSQL(DELETE_TABLE_DUBLIN_BUS);
        db.execSQL(DELETE_TABLE_LUAS);
        db.execSQL(DELETE_TABLE_TRAIN);
        db.execSQL(DELETE_TABLE_LEAP_BALANCE);
        db.execSQL(DELETE_TABLE_LEAP_LOGIN);
        onCreate(db);
    }

    public void insertRecord(String tableName,
                             String stopNumber, String station,
                             String route, String direction,
                             String destination, int frequency,
                             String cardNumber, String balance,
                             String userEmail, String userPassword){
        SQLiteDatabase writeDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        switch (tableName){
            case Database.BusEireannFavourites.TABLE_NAME:
                contentValues.put(Database.BusEireannFavourites.STOP_NUMBER, stopNumber);
                contentValues.put(Database.BusEireannFavourites.ROUTE, route);
                contentValues.put(Database.BusEireannFavourites.DESTINATION, destination);
                contentValues.put(Database.BusEireannFavourites.FREQUENCY, frequency);
                break;
            case Database.DartFavourites.TABLE_NAME:
                contentValues.put(Database.DartFavourites.STATION, station);
                contentValues.put(Database.DartFavourites.DIRECTION, direction);
                contentValues.put(Database.DartFavourites.FREQUENCY, frequency);
                break;
            case Database.DublinBusFavourites.TABLE_NAME:
                contentValues.put(Database.DublinBusFavourites.STOP_NUMBER, stopNumber);
                contentValues.put(Database.DublinBusFavourites.ROUTE, route);
                contentValues.put(Database.DublinBusFavourites.FREQUENCY, frequency);
                break;
            case Database.LuasFavourites.TABLE_NAME:
                contentValues.put(Database.LuasFavourites.STATION, station);
                contentValues.put(Database.LuasFavourites.DIRECTION, direction);
                contentValues.put(Database.LuasFavourites.FREQUENCY, frequency);
                break;
            case Database.TrainFavourites.TABLE_NAME:
                contentValues.put(Database.LuasFavourites.STATION, station);
                contentValues.put(Database.LuasFavourites.DIRECTION, direction);
                contentValues.put(Database.LuasFavourites.FREQUENCY, frequency);
                break;
            case Database.LeapBalance.TABLE_NAME:
                contentValues.put(Database.LeapBalance.CARD_NUMBER, cardNumber);
                contentValues.put(Database.LeapBalance.BALANCE, balance);
                break;
            case Database.LeapLogin.TABLE_NAME:
                contentValues.put(Database.LeapLogin.CARD_NUMBER, cardNumber);
                contentValues.put(Database.LeapLogin.USER_EMAIL, userEmail);
                contentValues.put(Database.LeapLogin.USER_PASSWORD, userPassword);
            default:
                break;
        }
        writeDb.close();
    }

    public void removeRecord(String tableClassName, int id){
        SQLiteDatabase writeDb = this.getWritableDatabase();

        String removeRowQuery =
                "DELETE FROM " + tableClassName +
                        " WHERE " + tableClassName + ".ID = " + id + ";";
        writeDb.execSQL(removeRowQuery);
        writeDb.close();
    }

    public void modifyRecord(String tableName,
                             String stopNumber, String station,
                             String route, String direction,
                             String destination){

    }

    public void modifyFrequency(String tableClassName, int id, int frequency){
        SQLiteDatabase writeDb = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(tableClassName + ".ID", frequency);

        String[] whereArgs = {String.valueOf(id)};
        writeDb.update(tableClassName, contentValues,
                tableClassName + ".ID" + "=" + "?", whereArgs);
        writeDb.close();
    }

    public void clearTable(String tableName){
        SQLiteDatabase writeDb = this.getWritableDatabase();
        String clearTableQuery =
                "DELETE FROM " + tableName + ";";
        writeDb.execSQL(clearTableQuery);
        writeDb.close();
    }

    public void printTableContents(String tableName){
        SQLiteDatabase readDb = this.getReadableDatabase();
        Cursor cursor = readDb.rawQuery("SELECT * FROM " + tableName, null);
        cursor.moveToFirst();

        int rowNum = cursor.getCount();
        int colNum = cursor.getColumnCount();

        System.out.println("# of rows in " + tableName + " is " + rowNum);
        System.out.println("# of columns in " + tableName + " is " + colNum);

        for (int r = 0; r < rowNum; r++){
            for(int c = 0; c < colNum; c++){
                System.out.println(c + ". " + cursor.getString(c) + "; ");
            }
            System.out.println("\n------------");
            cursor.moveToNext();
        }
        cursor.close();
        readDb.close();
    }

}
