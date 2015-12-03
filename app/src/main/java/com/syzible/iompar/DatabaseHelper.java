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

    //COL NAMES
    public static final int COL_DUBLIN_BUS_ID = 0;
    public static final int COL_DUBLIN_BUS_STOP_NUMBER = 1;
    public static final int COL_DUBLIN_BUS_ROUTE = 2;
    public static final int COL_DUBLIN_BUS_FREQUENCY = 3;

    public static final int COL_BUS_EIREANN_ID = 0;
    public static final int COL_BUS_EIREANN_STOP_NUMBER = 1;
    public static final int COL_BUS_EIREANN_ROUTE = 2;
    public static final int COL_BUS_EIREANN_DESTINATION = 3;
    public static final int COL_BUS_EIREANN_FREQUENCY = 4;

    public static final int COL_LUAS_ID = 0;
    public static final int COL_LUAS_STATION = 1;
    public static final int COL_LUAS_LINE = 2;
    public static final int COL_LUAS_DIRECTION = 3;
    public static final int COL_LUAS_FREQUENCY = 4;

    public static final int COL_DART_ID = 0;
    public static final int COL_DART_STATION = 1;
    public static final int COL_DART_LINE = 2;
    public static final int COL_DART_DIRECTION = 3;
    public static final int COL_DART_FREQUENCY = 4;

    public static final int COL_TRAIN_ID = 0;
    public static final int COL_TRAIN_STATION = 1;
    public static final int COL_TRAIN_LINE = 2;
    public static final int COL_TRAIN_DIRECTION = 3;
    public static final int COL_TRAIN_FREQUENCY = 4;

    final static String[] TABLES = {
            Database.BusEireannFavourites.TABLE_NAME,
            Database.DartFavourites.TABLE_NAME,
            Database.DublinBusFavourites.TABLE_NAME,
            Database.LuasFavourites.TABLE_NAME,
            Database.LeapBalance.TABLE_NAME,
            Database.LeapLogin.TABLE_NAME
    };

    //QUERIES
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
                    Database.LuasFavourites.LINE + " TEXT," +
                    Database.LuasFavourites.STATION + " TEXT," +
                    Database.LuasFavourites.DIRECTION + " TEXT," +
                    Database.LuasFavourites.FREQUENCY + " INTEGER);";

    public static final String CREATE_TABLE_DART_FAVOURITES =
            "CREATE TABLE " +
                    Database.DartFavourites.TABLE_NAME + "(" +
                    Database.DartFavourites.ID + " INTEGER PRIMARY KEY," +
                    Database.DartFavourites.LINE + " TEXT," +
                    Database.DartFavourites.STATION + " TEXT," +
                    Database.DartFavourites.DIRECTION + " TEXT," +
                    Database.DartFavourites.FREQUENCY + " INTEGER);";

    public static final String CREATE_TABLE_TRAIN_FAVOURITES =
            "CREATE TABLE " +
                    Database.TrainFavourites.TABLE_NAME + "(" +
                    Database.TrainFavourites.ID + " INTEGER PRIMARY KEY," +
                    Database.TrainFavourites.LINE + " TEXT," +
                    Database.TrainFavourites.STATION + " TEXT," +
                    Database.TrainFavourites.DIRECTION + " TEXT," +
                    Database.TrainFavourites.FREQUENCY + " INTEGER);";

    public static final String CREATE_TABLE_LEAP_BALANCE =
            "CREATE TABLE " +
                    Database.LeapBalance.TABLE_NAME + "(" +
                    Database.LeapBalance.ID + " INTEGER PRIMARY KEY," +
                    Database.LeapBalance.CARD_NUMBER + " INTEGER," +
                    Database.LeapBalance.TIME_ADDED + " INTEGER," +
                    Database.LeapBalance.DATE + " TEXT," +
                    Database.LeapBalance.TOP_UPS + " REAL," +
                    Database.LeapBalance.EXPENDITURE + "REAL," +
                    Database.LeapBalance.BALANCE_CHANGE + " REAL," +
                    Database.LeapBalance.BALANCE + " REAL," +
                    Database.LeapBalance.IS_NEGATIVE + " BOOLEAN);";

    public static final String CREATE_TABLE_LEAP_LOGIN =
            "CREATE TABLE " +
                    Database.LeapLogin.TABLE_NAME + "(" +
                    Database.LeapLogin.ID + " INTEGER PRIMARY KEY," +
                    Database.LeapLogin.CARD_NUMBER + " INTEGER," +
                    Database.LeapLogin.USER_EMAIL + " VARCHAR(320)," +
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

    public static final String SUM_EXPENDITURES_COLUMN =
            "SELECT SUM " + Database.LeapBalance.EXPENDITURE + " FROM " + Database.LeapBalance.TABLE_NAME + ";";

    public static final String SUM_TOP_UPS_COLUMN =
            "SELECT SUM " + Database.LeapBalance.TOP_UPS + " FROM " + Database.LeapBalance.TABLE_NAME + ";";

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
        if(oldVersion < newVersion){
            for(int i = 0; i <= TABLES.length; i++){
                final String UPGRADE_TABLE =
                        "ALTER TABLE " + TABLES[i] + ";";
                db.execSQL(UPGRADE_TABLE);
            }
        }
    }

    public void insertRecord(String tableName,
                             String stopNumber, String station, String line,
                             String route, String direction,
                             String destination, int frequency, String balance,
                             String date, double topUp, double expenditure,
                             double balanceChange, boolean negative,
                             String cardNumber, String userEmail, String userPassword){
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
                contentValues.put(Database.DartFavourites.LINE, line);
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
                contentValues.put(Database.LuasFavourites.LINE, line);
                contentValues.put(Database.LuasFavourites.STATION, station);
                contentValues.put(Database.LuasFavourites.DIRECTION, direction);
                contentValues.put(Database.LuasFavourites.FREQUENCY, frequency);
                break;
            case Database.TrainFavourites.TABLE_NAME:
                contentValues.put(Database.LuasFavourites.LINE, line);
                contentValues.put(Database.LuasFavourites.STATION, station);
                contentValues.put(Database.LuasFavourites.DIRECTION, direction);
                contentValues.put(Database.LuasFavourites.FREQUENCY, frequency);
                break;
            case Database.LeapBalance.TABLE_NAME:
                contentValues.put(Database.LeapBalance.CARD_NUMBER, cardNumber);
                contentValues.put(Database.LeapBalance.TIME_ADDED, System.currentTimeMillis());
                contentValues.put(Database.LeapBalance.DATE, date);
                contentValues.put(Database.LeapBalance.TOP_UPS, topUp);
                contentValues.put(Database.LeapBalance.EXPENDITURE, expenditure);
                contentValues.put(Database.LeapBalance.BALANCE_CHANGE, balanceChange);
                contentValues.put(Database.LeapBalance.BALANCE, balance);
                contentValues.put(Database.LeapBalance.IS_NEGATIVE, negative);
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

    public void removeRecord(String tableName, String idCol, int id){
        SQLiteDatabase writeDb = this.getWritableDatabase();

        String removeRowQuery =
                "DELETE FROM " + tableName +
                        " WHERE " + idCol + "=" + id + ";";
        writeDb.execSQL(removeRowQuery);
        writeDb.close();
    }

    public void modifyRecord(String tableName, String colId, int id,
                             String stopNumber, String station, String line,
                             String route, String direction,
                             String destination,
                             String cardNumber, String balance,
                             String date, double topUp, double expenditure,
                             double balanceChange, boolean negative,
                             String userEmail, String userPassword){

        SQLiteDatabase writeDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        switch (tableName){
            case Database.BusEireannFavourites.TABLE_NAME:
                contentValues.put(Database.BusEireannFavourites.STOP_NUMBER, stopNumber);
                contentValues.put(Database.BusEireannFavourites.ROUTE, route);
                contentValues.put(Database.BusEireannFavourites.DESTINATION, destination);
                break;
            case Database.DartFavourites.TABLE_NAME:
                contentValues.put(Database.DartFavourites.LINE, line);
                contentValues.put(Database.DartFavourites.STATION, station);
                contentValues.put(Database.DartFavourites.DIRECTION, direction);
                break;
            case Database.DublinBusFavourites.TABLE_NAME:
                contentValues.put(Database.DublinBusFavourites.STOP_NUMBER, stopNumber);
                contentValues.put(Database.DublinBusFavourites.ROUTE, route);
                break;
            case Database.LuasFavourites.TABLE_NAME:
                contentValues.put(Database.LuasFavourites.LINE, line);
                contentValues.put(Database.LuasFavourites.STATION, station);
                contentValues.put(Database.LuasFavourites.DIRECTION, direction);
                break;
            case Database.TrainFavourites.TABLE_NAME:
                contentValues.put(Database.TrainFavourites.LINE, line);
                contentValues.put(Database.TrainFavourites.STATION, station);
                contentValues.put(Database.TrainFavourites.DIRECTION, direction);
                break;
            case Database.LeapBalance.TABLE_NAME:
                contentValues.put(Database.LeapBalance.CARD_NUMBER, cardNumber);
                contentValues.put(Database.LeapBalance.TIME_ADDED, System.currentTimeMillis());
                contentValues.put(Database.LeapBalance.DATE, date);
                contentValues.put(Database.LeapBalance.TOP_UPS, topUp);
                contentValues.put(Database.LeapBalance.EXPENDITURE, expenditure);
                contentValues.put(Database.LeapBalance.BALANCE_CHANGE, balanceChange);
                contentValues.put(Database.LeapBalance.BALANCE, balance);
                contentValues.put(Database.LeapBalance.IS_NEGATIVE, negative);
                break;
            case Database.LeapLogin.TABLE_NAME:
                contentValues.put(Database.LeapLogin.CARD_NUMBER, cardNumber);
                contentValues.put(Database.LeapLogin.USER_EMAIL, userEmail);
                contentValues.put(Database.LeapLogin.USER_PASSWORD, userPassword);
            default:
                break;
        }
        String[] whereArgs = {String.valueOf(id)};
        writeDb.update(tableName, contentValues,
                colId + "=" + "?", whereArgs);
        writeDb.close();

    }

    public void modifyFrequency(String tableName, String tableClassName, String colId, int id, int frequency){
        SQLiteDatabase writeDb = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(tableClassName + ".ID", frequency);

        String[] whereArgs = {String.valueOf(id)};
        writeDb.update(tableName, contentValues,
                colId + "=" + "?", whereArgs);
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
