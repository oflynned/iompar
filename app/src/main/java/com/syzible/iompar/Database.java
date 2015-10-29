package com.syzible.iompar;

import android.provider.BaseColumns;

/**
 * Created by ed on 28/10/15.
 */
public class Database {
    public static final String DATABASE_NAME = "iompar_db";

    public Database(){}

    //table to allow for regular stops to be accessed quickly
    public static abstract class Favourites implements BaseColumns {
        //column names
        public static final String TABLE_NAME = "favourites_log";
        //allocate an id to each entity added to the table
        public static final String STOP_ID = "stop_id"; //int
        //bus, luas, train...
        public static final String TRANSIT_TYPE = "transit_type"; //string
        //ie charlemont (ordered from stephen's green to bride's glen) via luas would be #3
        //ie st john's ambulance via Dublin Bus would be #907 from the website etc
        public static final String STOP_NUMBER = "stop"; //int
    }

    //table to keep a regular transit set as a notification over time if added
    //ie I get a bus everyday at 8:41am with a variance of x mins, therefore I need to be notified
    public static abstract class RegularNotifications implements BaseColumns {
        public static final String TABLE_NAME = "regular_log";
        public static final String TRANSIT_ID = "transit_id"; //int
        public static final String TRANSIT_TYPE = "transit_type"; //string, train bus tram...
        public static final String TIME = "time"; //time of departure; 08:41 ... 18:10 ...
    }

    //table to access current leapcard balance from spending so you can track
    public static abstract class LeapBalance implements BaseColumns {
        public static final String TABLE_NAME = "leap_balance_log";
        public static final String LEAP_ID = "leap_id"; //id for row
        public static final String LEAP_NUMBER = "leap_number"; //long, 14 digit long registration number
        public static final String LEAP_BALANCE = "balance"; //double, balance on leapcard
    }

    //table to store login information to retrieve actual balance via json
    public static abstract class LeapLogin implements BaseColumns {
        public static final String TABLE_NAME = "leap_log_info";
        public static final String LEAP_ID = "leap_id"; //most likely will only ever be 1
        public static final String LEAP_NUMBER = "leap_number"; //long, 14 digit long registration number
        public static final String USER_EMAIL = "email"; //string, for login with leapcard.ie
        public static final String USER_PASSWORD = "password"; //string, password for login
    }
}
