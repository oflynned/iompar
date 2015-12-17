package com.syzible.iompar;

import android.provider.BaseColumns;

/**
 * Created by ed on 28/10/15.
 */
public class Database {
    public static final String DATABASE_NAME = "iompar_db";

    public Database(){}

    //table to allow for regular stops to be accessed quickly
    public static abstract class DublinBusFavourites implements BaseColumns {
        public static final String TABLE_NAME = "dublin_bus_favourites";
        public static final String ID = "id";
        public static final String STOP_NUMBER = "stop_number";
        public static final String ROUTE = "route";
        public static final String FREQUENCY = "frequency";
    }

    public static abstract class BusEireannFavourites implements BaseColumns {
        public static final String TABLE_NAME = "bus_eireann_favourites";
        public static final String ID = "id";
        public static final String STOP_NUMBER = "stop_number";
        public static final String ROUTE = "route";
        public static final String DESTINATION = "destination";
        public static final String FREQUENCY = "frequency";
    }

    public static abstract class LuasFavourites implements BaseColumns {
        public static final String TABLE_NAME = "luas_favourites";
        public static final String ID = "id";
        public static final String LINE = "line";
        public static final String STATION = "station";
        public static final String DIRECTION = "direction";
        public static final String FREQUENCY = "frequency";
    }

    public static abstract class DartFavourites implements BaseColumns {
        public static final String TABLE_NAME = "dart_favourites";
        public static final String ID = "id";
        public static final String LINE = "line";
        public static final String STATION = "station";
        public static final String DIRECTION = "direction";
        public static final String FREQUENCY = "frequency";
    }

    public static abstract class TrainFavourites implements BaseColumns {
        public static final String TABLE_NAME = "train_favourites";
        public static final String ID = "id";
        public static final String LINE = "line";
        public static final String STATION = "station";
        public static final String DIRECTION = "direction";
        public static final String FREQUENCY = "frequency";
    }

    public static abstract class LeapBalance implements BaseColumns {
        public static final String TABLE_NAME = "leap_balance";
        public static final String ID = "id";
        public static final String CARD_NUMBER = "number";
        public static final String TIME_ADDED = "time_added";
        public static final String DATE = "date";
        public static final String TOP_UPS = "top_ups";
        public static final String EXPENDITURE = "expenditure";
        public static final String BALANCE_CHANGE = "balance_change";
        public static final String BALANCE = "balance";
        public static final String IS_NEGATIVE = "is_negative";
    }

    public static abstract class LeapLogin implements BaseColumns {
        public static final String TABLE_NAME = "leap_login_info";
        public static final String ID = "id";
        public static final String CARD_NUMBER = "number";
        public static final String USER_NAME = "name";
        public static final String USER_EMAIL = "email";
        public static final String USER_PASSWORD = "password";
        public static final String IS_ACTIVE = "is_active";
    }

    public static abstract class LuasSingleFares implements BaseColumns {
        public static final String TABLE_NAME = "luas_single_fares";
        public static final String ID = "id";
        public static final String ADULT = "adult";
        public static final String CHILD = "child";
        public static final String ADULT_STUDENT_OFF_PEAK = "adult_student_off_peak";
        public static final String ADULT_STUDENT_PEAK = "adult_student_peak";
        public static final String CHILD_OFF_PEAK = "child_off_peak";
        public static final String CHILD_PEAK = "child_peak";
    }

    public static abstract class LuasReturnFares implements BaseColumns {
        public static final String TABLE_NAME = "luas_return_fares";
        public static final String ID = "id";
        public static final String ADULT = "adult";
        public static final String CHILD = "child";
    }

    public static abstract class LeapCaps implements BaseColumns {
        public static final String TABLE_NAME = "leap_caps";
        public static final String ID = "id";
        public static final String LUAS_DAILY_CAP = "luas_daily_cap";
        public static final String LUAS_WEEKLY_CAP = "luas_weekly_cap";
        public static final String DUBLIN_BUS_LUAS_DART_COMM_RAIL_DAILY_CAP = "dublin_bus_luas_dart_comm_rail_daily_cap";
        public static final String DUBLIN_BUS_LUAS_DART_COMM_RAIL_WEEKLY_CAP = "dublin_bus_luas_dart_comm_rail_weekly_cap";
    }
}
