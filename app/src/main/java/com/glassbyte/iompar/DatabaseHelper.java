package com.glassbyte.iompar;

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

    public static final int COL_LEAP_BALANCE_ID = 0;
    public static final int COL_LEAP_BALANCE_CARD_NUMBER = 1;
    public static final int COL_LEAP_BALANCE_TIME_ADDED = 2;
    public static final int COL_LEAP_BALANCE_TOP_UPS = 3;
    public static final int COL_LEAP_BALANCE_EXPENDITURE = 4;
    public static final int COL_LEAP_BALANCE_BALANCE_CHANGE = 5;
    public static final int COL_LEAP_BALANCE_BALANCE = 6;
    public static final int COL_LEAP_BALANCE_IS_NEGATIVE = 7;

    public static final int COL_EXPENDITURES_ID = 0;
    public static final int COL_EXPENDITURES_IS_LEAP = 1;
    public static final int COL_EXPENDITURES_CARD_NUMBER = 2;
    public static final int COL_EXPENDITURES_TIME_ADDED = 3;
    public static final int COL_EXPENDITURES_EXPENDITURE = 4;

    public static final int COL_LEAP_LOGIN_ID = 0;
    public static final int COL_LEAP_LOGIN_USER_NAME = 1;
    public static final int COL_LEAP_LOGIN_CARD_NUMBER = 2;
    public static final int COL_LEAP_LOGIN_EMAIL = 3;
    public static final int COL_LEAP_LOGIN_PASSWORD = 4;
    public static final int COL_LEAP_LOGIN_IS_ACTIVE = 5;
    public static final int COL_LEAP_LOGIN_BALANCE = 6;

    public static final int COL_LUAS_SINGLE_FARES_ID = 0;
    public static final int COL_LUAS_SINGLE_FARES_ADULT = 1;
    public static final int COL_LUAS_SINGLE_FARES_CHILD = 2;
    public static final int COL_LUAS_SINGLE_FARES_ADULT_STUDENT_OFF_PEAK = 3;
    public static final int COL_LUAS_SINGLE_FARES_ADULT_STUDENT_PEAK = 4;
    public static final int COL_LUAS_SINGLE_FARES_CHILD_OFF_PEAK = 5;
    public static final int COL_LUAS_SINGLE_FARES_CHILD_PEAK = 6;

    public static final int COL_LUAS_RETURN_FARES_ID = 0;
    public static final int COL_LUAS_RETURN_FARES_ADULT = 1;
    public static final int COL_LUAS_RETURN_FARES_CHILD = 2;

    public static final int COL_LUAS_LEAP_CAPS_ID = 0;
    public static final int COL_LUAS_LEAP_CAPS_LUAS_DAILY = 1;
    public static final int COL_LUAS_LEAP_CAPS_LUAS_WEEKLY = 2;
    public static final int COL_LUAS_LEAP_CAPS_DUBLIN_BUS_LUAS_DART_COMM_RAIL_DAILY_CAP = 3;
    public static final int COL_LUAS_LEAP_CAPS_DUBLIN_BUS_LUAS_DART_COMM_RAIL_WEEKLY_CAP = 4;

    final static String[] TABLES = {
            Database.BusEireannFavourites.TABLE_NAME,
            Database.DartFavourites.TABLE_NAME,
            Database.DublinBusFavourites.TABLE_NAME,
            Database.LuasFavourites.TABLE_NAME,
            Database.LeapBalance.TABLE_NAME,
            Database.Expenditures.TABLE_NAME,
            Database.LeapLogin.TABLE_NAME,
            Database.LuasSingleFares.TABLE_NAME,
            Database.LuasReturnFares.TABLE_NAME,
            Database.LeapCaps.TABLE_NAME
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
                    Database.LeapBalance.TOP_UPS + " REAL," +
                    Database.LeapBalance.EXPENDITURE + "REAL," +
                    Database.LeapBalance.BALANCE_CHANGE + " REAL," +
                    Database.LeapBalance.BALANCE + " REAL," +
                    Database.LeapBalance.IS_NEGATIVE + " BOOLEAN);";

    public static final String CREATE_TABLE_EXPENDITURES =
            "CREATE TABLE " +
                    Database.Expenditures.TABLE_NAME + "(" +
                    Database.Expenditures.ID + " INTEGER PRIMARY KEY," +
                    Database.Expenditures.IS_LEAP + " BOOLEAN," +
                    Database.Expenditures.CARD_NUMBER + " INTEGER," +
                    Database.Expenditures.TIME_ADDED + " INTEGER," +
                    Database.Expenditures.EXPENDITURE + " DECIMAL(5,2));";

    public static final String CREATE_TABLE_LEAP_LOGIN =
            "CREATE TABLE " +
                    Database.LeapLogin.TABLE_NAME + "(" +
                    Database.LeapLogin.ID + " INTEGER PRIMARY KEY," +
                    Database.LeapLogin.CARD_NUMBER + " INTEGER," +
                    Database.LeapLogin.USER_NAME + " TEXT," +
                    Database.LeapLogin.USER_EMAIL + " VARCHAR(320)," +
                    Database.LeapLogin.USER_PASSWORD + " VARCHAR(320)," +
                    Database.LeapLogin.IS_ACTIVE + " BOOLEAN" +
                    Database.LeapLogin.BALANCE + " REAL);";

    public static final String CREATE_TABLE_LUAS_SINGLE_FARES =
            "CREATE TABLE " +
                    Database.LuasSingleFares.TABLE_NAME + "(" +
                    Database.LuasSingleFares.ID + " INTEGER PRIMARY KEY," +
                    Database.LuasSingleFares.ADULT + " REAL," +
                    Database.LuasSingleFares.CHILD + " REAL," +
                    Database.LuasSingleFares.ADULT_STUDENT_OFF_PEAK + " REAL," +
                    Database.LuasSingleFares.ADULT_STUDENT_PEAK + " REAL," +
                    Database.LuasSingleFares.CHILD_OFF_PEAK + " REAL," +
                    Database.LuasSingleFares.CHILD_PEAK + " REAL);";

    public static final String CREATE_TABLE_LUAS_RETURN_FARES =
            "CREATE TABLE " +
                    Database.LuasReturnFares.TABLE_NAME + "(" +
                    Database.LuasReturnFares.ID + " INTEGER PRIMARY KEY," +
                    Database.LuasReturnFares.ADULT + " REAL," +
                    Database.LuasReturnFares.CHILD + " REAL);";

    public static final String CREATE_TABLE_LEAP_CAPS =
            "CREATE TABLE " +
                    Database.LeapCaps.TABLE_NAME + "(" +
                    Database.LeapCaps.ID + " INTEGER PRIMARY KEY," +
                    Database.LeapCaps.LUAS_DAILY_CAP + " REAL," +
                    Database.LeapCaps.LUAS_WEEKLY_CAP + " REAL," +
                    Database.LeapCaps.DUBLIN_BUS_LUAS_DART_COMM_RAIL_DAILY_CAP + " REAL," +
                    Database.LeapCaps.DUBLIN_BUS_LUAS_DART_COMM_RAIL_WEEKLY_CAP + " REAL);";

    public static final String DELETE_TABLE_SINGLE_FARES =
            "DROP TABLE IF EXISTS " + Database.LuasSingleFares.TABLE_NAME + ";";

    public static final String DELETE_TABLE_RETURN_FARES =
            "DROP TABLE IF EXISTS " + Database.LuasReturnFares.TABLE_NAME + ";";

    public static final String DELETE_TABLE_LEAP_CAPS =
            "DROP TABLE IF EXISTS " + Database.LeapCaps.TABLE_NAME + ";";

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

    public static final String DELETE_TABLE_EXPENDITURES =
            "DROP TABLE IF EXISTS " + Database.Expenditures.TABLE_NAME + ";";

    public static final String DELETE_TABLE_LEAP_LOGIN =
            "DROP TABLE IF EXISTS " + Database.LeapLogin.TABLE_NAME + ";";

    public static final String SELECT_ALL_LUAS_SINGLE_FARES =
            "SELECT * FROM " + Database.LuasSingleFares.TABLE_NAME + ";";

    public static final String SELECT_ALL_LUAS_RETURN_FARES =
            "SELECT * FROM " + Database.LuasReturnFares.TABLE_NAME + ";";

    public static final String SELECT_ALL_LEAP_CAPS =
            "SELECT * FROM " + Database.LeapCaps.TABLE_NAME + ";";

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

    public static final String SELECT_ALL_EXPENDITURES =
            "SELECT * FROM " + Database.Expenditures.TABLE_NAME + " ORDER BY " + Database.Expenditures.ID + " DESC;";

    public static final String SELECT_ALL_LEAP_LOGIN =
            "SELECT * FROM " + Database.LeapLogin.TABLE_NAME + ";";

    public static final String SELECT_ALL_ACTIVE_LEAP_CARDS =
            "SELECT * FROM " + Database.LeapLogin.TABLE_NAME +
                    " WHERE " + Database.LeapLogin.IS_ACTIVE + " = 1;";

    public DatabaseHelper(Context context){
        super(context, Database.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BUS_EIREANN_FAVOURITES);
        db.execSQL(CREATE_TABLE_DART_FAVOURITES);
        db.execSQL(CREATE_TABLE_DUBLIN_BUS_FAVOURITES);
        db.execSQL(CREATE_TABLE_LUAS_FAVOURITES);
        db.execSQL(CREATE_TABLE_TRAIN_FAVOURITES);
        db.execSQL(CREATE_TABLE_LEAP_BALANCE);
        db.execSQL(CREATE_TABLE_EXPENDITURES);
        db.execSQL(CREATE_TABLE_LEAP_LOGIN);
        db.execSQL(CREATE_TABLE_LUAS_SINGLE_FARES);
        db.execSQL(CREATE_TABLE_LUAS_RETURN_FARES);
        db.execSQL(CREATE_TABLE_LEAP_CAPS);
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

    public void insertFares(String tableName,
                            //single cash/leap
                            String adultCashSingle, String childCashSingle, String adultStudentOffPeakLeapSingle,
                            String adultStudentPeakLeapSingle, String childOffPeakLeapSingle, String childPeakLeapSingle,
                            //return cash
                            String adultCashReturn, String childCashReturn,
                            //leap caps
                            String luasDailyCap, String luasWeeklyCap, String dbLuasDartCommDailyCap,
                            String dbLuasDartCommWeeklyCap){
        SQLiteDatabase writeDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        switch (tableName){
            case Database.LuasSingleFares.TABLE_NAME:
                contentValues.put(Database.LuasSingleFares.ADULT, adultCashSingle);
                contentValues.put(Database.LuasSingleFares.CHILD, childCashSingle);
                contentValues.put(Database.LuasSingleFares.ADULT_STUDENT_OFF_PEAK, adultStudentOffPeakLeapSingle);
                contentValues.put(Database.LuasSingleFares.ADULT_STUDENT_PEAK, adultStudentPeakLeapSingle);
                contentValues.put(Database.LuasSingleFares.CHILD_OFF_PEAK, childOffPeakLeapSingle);
                contentValues.put(Database.LuasSingleFares.CHILD_PEAK, childPeakLeapSingle);
                break;
            case Database.LuasReturnFares.TABLE_NAME:
                contentValues.put(Database.LuasReturnFares.ADULT, adultCashReturn);
                contentValues.put(Database.LuasReturnFares.CHILD, childCashReturn);
                break;
            case Database.LeapCaps.TABLE_NAME:
                contentValues.put(Database.LeapCaps.LUAS_DAILY_CAP, luasDailyCap);
                contentValues.put(Database.LeapCaps.LUAS_WEEKLY_CAP, luasWeeklyCap);
                contentValues.put(Database.LeapCaps.DUBLIN_BUS_LUAS_DART_COMM_RAIL_DAILY_CAP, dbLuasDartCommDailyCap);
                contentValues.put(Database.LeapCaps.DUBLIN_BUS_LUAS_DART_COMM_RAIL_WEEKLY_CAP, dbLuasDartCommWeeklyCap);
                break;
            default:
                System.out.println("Incorrect table name parametrised");
                break;
        }
        writeDb.insert(tableName, null, contentValues);
        writeDb.close();
    }

    public void insertExpenditure(boolean isLeap, String cardNumber, String expenditure){
        SQLiteDatabase writeDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Long time = System.currentTimeMillis();
        contentValues.put(Database.Expenditures.IS_LEAP, isLeap);
        contentValues.put(Database.Expenditures.CARD_NUMBER, cardNumber);
        contentValues.put(Database.Expenditures.TIME_ADDED, time);
        contentValues.put(Database.Expenditures.EXPENDITURE, expenditure);

        writeDb.insert(Database.Expenditures.TABLE_NAME, null, contentValues);
        writeDb.close();
    }

    public void insertRecord(String tableName,
                             String stopNumber, String station, String line,
                             String route, String direction,
                             String destination, int frequency, String balance,
                             double topUp, double expenditure,
                             double balanceChange, boolean negative,
                             String userName, String cardNumber, String userEmail,
                             String userPassword, boolean isActive){
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
                contentValues.put(Database.LeapBalance.TOP_UPS, topUp);
                contentValues.put(Database.LeapBalance.EXPENDITURE, expenditure);
                contentValues.put(Database.LeapBalance.BALANCE_CHANGE, balanceChange);
                contentValues.put(Database.LeapBalance.BALANCE, balance);
                contentValues.put(Database.LeapBalance.IS_NEGATIVE, negative);
                break;
            case Database.LeapLogin.TABLE_NAME:
                contentValues.put(Database.LeapLogin.CARD_NUMBER, cardNumber);
                contentValues.put(Database.LeapLogin.USER_NAME, userName);
                contentValues.put(Database.LeapLogin.USER_EMAIL, userEmail);
                contentValues.put(Database.LeapLogin.USER_PASSWORD, userPassword);
                contentValues.put(Database.LeapLogin.IS_ACTIVE, isActive);
            default:
                break;
        }
        writeDb.insert(tableName, null, contentValues);
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
                             String userName, String userEmail, String userPassword, boolean isActive){

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
                contentValues.put(Database.LeapBalance.TOP_UPS, topUp);
                contentValues.put(Database.LeapBalance.EXPENDITURE, expenditure);
                contentValues.put(Database.LeapBalance.BALANCE_CHANGE, balanceChange);
                contentValues.put(Database.LeapBalance.BALANCE, balance);
                contentValues.put(Database.LeapBalance.IS_NEGATIVE, negative);
                break;
            case Database.LeapLogin.TABLE_NAME:
                contentValues.put(Database.LeapLogin.CARD_NUMBER, cardNumber);
                contentValues.put(Database.LeapLogin.USER_NAME, userName);
                contentValues.put(Database.LeapLogin.USER_EMAIL, userEmail);
                contentValues.put(Database.LeapLogin.USER_PASSWORD, userPassword);
                contentValues.put(Database.LeapLogin.IS_ACTIVE, isActive);
            default:
                break;
        }
        String[] whereArgs = {String.valueOf(id)};
        writeDb.update(tableName, contentValues,
                colId + "=" + "?", whereArgs);
        writeDb.close();

    }

    public void modifyActive(String tableName, String colIsActive, String colId, int id, boolean active){
        SQLiteDatabase writeDb = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(colIsActive, active);

        String[] whereArgs = {String.valueOf(id)};
        writeDb.update(tableName, contentValues,
                colId + "=" + "?", whereArgs);
        writeDb.close();
    }

    public void modifyFrequency(String tableName, String colFreq, String colId, int id, int frequency){
        SQLiteDatabase writeDb = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(colFreq, frequency);

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
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            int rowNum = cursor.getCount();
            int colNum = cursor.getColumnCount();

            System.out.println("# of rows in " + tableName + " is " + rowNum);
            System.out.println("# of columns in " + tableName + " is " + colNum);

            for (int r = 0; r < rowNum; r++) {
                for (int c = 0; c < colNum; c++) {
                    System.out.println(c + ". " + cursor.getString(c) + "; ");
                }
                System.out.println("\n------------");
                cursor.moveToNext();
            }
        }
        cursor.close();
        readDb.close();
    }

}
