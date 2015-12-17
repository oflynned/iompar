package com.syzible.iompar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ed on 29/10/15.
 */
public class Fares extends Fragment {

    View view;

    int originId, destinationId;
    String direction, fare;

    public enum FareType {ADULT, STUDENT, CHILD, OTHER}

    public enum FareCaps {ON_PEAK, OFF_PEAK}

    public enum FarePayment {LEAP, CASH}

    public enum FareJourney {SINGLE, RETURN}

    public enum LuasFareCost {ONE_ZONE, TWO_ZONES, THREE_ZONES, FOUR_ZONES, FIVE_EIGHT_ZONES}

    FareType fareType;
    FareCaps fareCaps;
    FarePayment farePayment;
    FareJourney fareJourney;
    LuasFareCost luasFareCost;

    DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fare, null);

        setFareType(FareType.ADULT);
        setFareJourney(FareJourney.SINGLE);
        setFarePayment(FarePayment.CASH);
        setFareCaps(FareCaps.ON_PEAK);
        setLuasFareCost(LuasFareCost.FIVE_EIGHT_ZONES);
        calculateFare(getContext());

        System.out.println("Fare cost: €" + getFare());

        return view;
    }

    /**
     * takes the parameters of type of fare for the user, and returns the appropriate costs
     *
     * @param line        the Luas line being traversed
     * @param origin      departing station
     * @param destination destination station
     * @return the cost of given transit route in Euro
     */
    public String getZoneTraversal(Realtime.LuasDirections line, String origin, String destination, Context context) {
        //payment type
        setFareType(FareType.ADULT);
        setFareJourney(FareJourney.SINGLE);
        setFarePayment(FarePayment.CASH);

        //peak?
        if (isPeak()) {
            setFareCaps(FareCaps.ON_PEAK);
            System.out.println("It is currently peak hour");
        } else {
            setFareCaps(FareCaps.OFF_PEAK);
            System.out.println("It is currently NOT peak hour");
        }

        setOriginId(getStationIndex(line, origin));
        setDestinationId(getStationIndex(line, destination));
        System.out.println("origin id: " + getOriginId());
        System.out.println("destination id: " + getDestinationId());

        setDirection(getOriginId(), getDestinationId());
        System.out.println("Direction set: " + getDirection());
        System.out.println("Line parameter: " + line);

        switch (line) {
            //tallaght-point
            case POINT:
            case TALLAGHT:
                System.out.println("Origin zone: " + getTallaghtZoneId(getOriginId(), getDestinationId()));
                System.out.println("Destination zone: " + getTallaghtZoneId(getOriginId(), getDestinationId()));
                setLuasFareCost(getZoneDifference(getTallaghtZoneId(getOriginId(), getDestinationId()),
                        getTallaghtZoneId(getDestinationId(), getOriginId())));
                break;
            //saggart-connolly or inter station
            case CONNOLLY:
            case SAGGART:
            case HEUSTON:
                System.out.println("Origin zone: " + getSaggartZoneId(getOriginId(), getDestinationId()));
                System.out.println("Destination zone: " + getSaggartZoneId(getOriginId(), getDestinationId()));
                setLuasFareCost(getZoneDifference(getSaggartZoneId(getOriginId(), getDestinationId()),
                        getSaggartZoneId(getDestinationId(), getOriginId())));
                break;
            case STEPHENS_GREEN:
            case SANDYFORD:
            case BRIDES_GLEN:
                System.out.println("Origin zone: " + getGreenLineZoneId(getOriginId(), getDestinationId()));
                System.out.println("Destination zone: " + getGreenLineZoneId(getOriginId(), getDestinationId()));
                setLuasFareCost(getZoneDifference(getGreenLineZoneId(getOriginId(), getDestinationId()),
                        getGreenLineZoneId(getDestinationId(), getOriginId())));
                break;
            default:
                System.out.println("no zone line parsed?");
                return "error";
        }
        calculateFare(context);

        System.out.println("Fare cost: €" + getFare());
        return getFare();
    }

    /**
     * takes the given stations and returns their zones with respect to the user's direction on
     * the tram when traversing the Luas line
     *
     * @param originIndex      the station from which the user departs
     * @param destinationIndex the station to which the user travels
     * @return returns the ID from Globals.class
     */
    public int getTallaghtZoneId(int originIndex, int destinationIndex) {
        System.out.println("called tallaght zone id calc");
        if (originIndex >= Globals.THE_POINT_TALLAGHT_ID && originIndex < Globals.GEORGES_DOCK_TALLAGHT_ID) {
            return Globals.DOCKLANDS_ID;
        } else if (originIndex >= Globals.BUSARAS_TALLAGHT_ID && originIndex < Globals.HEUSTON_TALLAGHT_ID) {
            return Globals.CENTRAL_1_ID;
        } else if (originIndex > Globals.HEUSTON_TALLAGHT_ID && originIndex < Globals.SUIR_ROAD_TALLAGHT_ID) {
            return Globals.RED_2_ID;
        } else if (originIndex > Globals.SUIR_ROAD_TALLAGHT_ID && originIndex < Globals.RED_COW_TALLAGHT_ID) {
            return Globals.RED_3_ID;
        } else if (originIndex > Globals.RED_COW_TALLAGHT_ID) {
            return Globals.RED_4_ID;
        }
        //george's dock transition station
        else if (originIndex == Globals.GEORGES_DOCK_TALLAGHT_ID &&
                destinationIndex < Globals.GEORGES_DOCK_TALLAGHT_ID) {
            return Globals.DOCKLANDS_ID;
        } else if (originIndex == Globals.GEORGES_DOCK_TALLAGHT_ID &&
                destinationIndex > Globals.GEORGES_DOCK_TALLAGHT_ID) {
            return Globals.CENTRAL_1_ID;
        }
        //heuston transition station
        else if (originIndex == Globals.HEUSTON_TALLAGHT_ID &&
                destinationIndex < Globals.HEUSTON_TALLAGHT_ID) {
            return Globals.CENTRAL_1_ID;
        } else if (originIndex == Globals.HEUSTON_TALLAGHT_ID &&
                destinationIndex > Globals.HEUSTON_TALLAGHT_ID) {
            return Globals.RED_2_ID;
        }
        //suir road transition station
        else if (originIndex == Globals.SUIR_ROAD_TALLAGHT_ID &&
                destinationIndex < Globals.SUIR_ROAD_TALLAGHT_ID) {
            return Globals.RED_2_ID;
        } else if (originIndex == Globals.SUIR_ROAD_TALLAGHT_ID &&
                destinationIndex > Globals.SUIR_ROAD_TALLAGHT_ID) {
            return Globals.RED_3_ID;
        }
        //red cow transition station
        else if (originIndex == Globals.RED_COW_TALLAGHT_ID &&
                destinationIndex < Globals.RED_COW_TALLAGHT_ID) {
            return Globals.RED_3_ID;
        } else if (originIndex == Globals.RED_COW_TALLAGHT_ID &&
                destinationIndex > Globals.RED_COW_TALLAGHT_ID) {
            return Globals.RED_4_ID;
        }
        return 0;
    }

    public int getSaggartZoneId(int originIndex, int destinationIndex) {
        System.out.println("called saggart zone id calc");
        if (originIndex >= Globals.CONNOLLY_SAGGART_ID && originIndex < Globals.HEUSTON_SAGGART_ID) {
            return Globals.CENTRAL_1_ID;
        } else if (originIndex > Globals.HEUSTON_SAGGART_ID && originIndex < Globals.SUIR_ROAD_SAGGART_ID) {
            return Globals.RED_2_ID;
        } else if (originIndex > Globals.SUIR_ROAD_SAGGART_ID && originIndex < Globals.RED_COW_SAGGART_ID) {
            return Globals.RED_3_ID;
        } else if (originIndex > Globals.RED_COW_SAGGART_ID) {
            return Globals.RED_4_ID;
        }
        //heuston transition station
        else if (originIndex == Globals.HEUSTON_SAGGART_ID &&
                destinationIndex < Globals.HEUSTON_SAGGART_ID) {
            return Globals.CENTRAL_1_ID;
        } else if (originIndex == Globals.HEUSTON_SAGGART_ID &&
                destinationIndex > Globals.HEUSTON_SAGGART_ID) {
            return Globals.RED_2_ID;
        }
        //suir road transition station
        else if (originIndex == Globals.SUIR_ROAD_SAGGART_ID &&
                destinationIndex < Globals.SUIR_ROAD_SAGGART_ID) {
            return Globals.RED_2_ID;
        } else if (originIndex == Globals.SUIR_ROAD_SAGGART_ID &&
                destinationIndex > Globals.SUIR_ROAD_SAGGART_ID) {
            return Globals.RED_3_ID;
        }
        //red cow transition station
        else if (originIndex == Globals.RED_COW_SAGGART_ID &&
                destinationIndex < Globals.RED_COW_SAGGART_ID) {
            return Globals.RED_3_ID;
        } else if (originIndex == Globals.RED_COW_SAGGART_ID &&
                destinationIndex > Globals.RED_COW_SAGGART_ID) {
            return Globals.RED_4_ID;
        }
        return 0;
    }

    public int getGreenLineZoneId(int originIndex, int destinationIndex) {
        System.out.println("called sandyford zone id calc");
        if (originIndex >= Globals.STEPHENS_GREEN_ID && originIndex < Globals.CHARLEMONT_ID) {
            return Globals.CENTRAL_1_ID;
        } else if (originIndex > Globals.CHARLEMONT_ID && originIndex < Globals.DUNDRUM_ID) {
            return Globals.GREEN_2_ID;
        } else if (originIndex > Globals.DUNDRUM_ID && originIndex < Globals.SANDYFORD_ID) {
            return Globals.GREEN_3_ID;
        } else if (originIndex > Globals.SANDYFORD_ID && originIndex <= Globals.BALLYOGAN_WOOD_ID) {
            return Globals.GREEN_4_ID;
        } else if (originIndex >= Globals.CARRICKMINES_ID) {
            return Globals.GREEN_5_ID;
        }
        //charlemont transition station
        else if (originIndex == Globals.CHARLEMONT_ID &&
                destinationIndex < Globals.CHARLEMONT_ID) {
            return Globals.CENTRAL_1_ID;
        } else if (originIndex == Globals.CHARLEMONT_ID &&
                destinationIndex > Globals.CHARLEMONT_ID) {
            return Globals.GREEN_2_ID;
        }
        //dundrum transition station
        else if (originIndex == Globals.DUNDRUM_ID &&
                destinationIndex < Globals.DUNDRUM_ID) {
            return Globals.GREEN_2_ID;
        } else if (originIndex == Globals.DUNDRUM_ID &&
                destinationIndex > Globals.DUNDRUM_ID) {
            return Globals.GREEN_3_ID;
        }
        //sandyford transition station
        else if (originIndex == Globals.SANDYFORD_ID &&
                destinationIndex < Globals.SANDYFORD_ID) {
            return Globals.GREEN_3_ID;
        } else if (originIndex == Globals.SANDYFORD_ID &&
                destinationIndex > Globals.SANDYFORD_ID) {
            return Globals.GREEN_4_ID;
        }
        return 0;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    public int getOriginId() {
        return originId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDirection(int start, int end) {
        if (start < end) {
            this.direction = "away from start";
        } else {
            this.direction = "towards start";
        }
    }

    public String getDirection() {
        return direction;
    }

    /**
     * Returns the difference in zone index to calculate the amount of zones being traversed
     *
     * @param origin      the origin station index from the array
     * @param destination the destination station as an index of the array
     * @return the absolute value of zone difference
     */
    public LuasFareCost getZoneDifference(int origin, int destination) {
        int difference = origin - destination;
        if (difference < 0) {
            difference = difference * -1;
        }
        System.out.println("Zone origin: " + origin);
        System.out.println("Zone destination: " + destination);
        System.out.println("Zone difference: " + difference);

        switch (difference) {
            case 0:
                return LuasFareCost.ONE_ZONE;
            case 1:
                return LuasFareCost.TWO_ZONES;
            case 2:
                return LuasFareCost.THREE_ZONES;
            case 3:
                return LuasFareCost.FOUR_ZONES;
            case 4:
            case 5:
            case 6:
            case 7:
                return LuasFareCost.FIVE_EIGHT_ZONES;
            default:
                return null;
        }
    }

    /**
     * takes the station and searches the given array to find the index of the station
     *
     * @param line    line to be searched
     * @param station station index to be found
     * @return the index of the station of the whole array
     */
    public int getStationIndex(Realtime.LuasDirections line, String station) {
        System.out.println("LINE: " + line);
        int index = 0;
        switch (line) {
            case TALLAGHT:
                for (String searchStation : Globals.redLineStationsTallaghtPoint) {
                    index++;
                    if (searchStation.contains(station)) {
                        return index;
                    }
                }
                break;
            case SAGGART:
            case CONNOLLY:
                for (String searchStation : Globals.redLineStationsSaggartConnolly) {
                    index++;
                    if (searchStation.contains(station)) {
                        return index;
                    }
                }
                break;
            case POINT:
                for (String searchStation : Globals.redLineStationsTallaghtPoint) {
                    index++;
                    if (searchStation.contains(station)) {
                        return index;
                    }
                }
                break;
            case BRIDES_GLEN:
            case SANDYFORD:
            case STEPHENS_GREEN:
                for (String searchStation : Globals.greenLineStationsBridesGlenStephensGreen) {
                    index++;
                    if (searchStation.contains(station)) {
                        return index;
                    }
                }
                break;
        }
        return -1;
    }

    /**
     * given all the appropriate properties, the correct fare should be returned to the user
     */
    public void calculateFare(Context context) {
        databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (getFareType()) {
            case ADULT:
                switch (getFareCaps()) {
                    case ON_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(0);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(1);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT));
                                        break;
                                    case LEAP:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT_STUDENT_PEAK));
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_RETURN_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_ADULT));
                                        break;
                                }
                                break;
                        }
                        break;
                    case OFF_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT));
                                        break;
                                    case LEAP:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT_STUDENT_OFF_PEAK));
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_RETURN_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_ADULT));
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
            case STUDENT:
                switch (getFareCaps()) {
                    case ON_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT));
                                        break;
                                    case LEAP:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT_STUDENT_PEAK));
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_RETURN_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_ADULT));
                                        break;
                                }
                                break;
                        }
                        break;
                    case OFF_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT));
                                        break;
                                    case LEAP:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT_STUDENT_OFF_PEAK));
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_RETURN_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_ADULT));
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
            case CHILD:
                switch (getFareCaps()) {
                    case ON_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_CHILD));
                                        break;
                                    case LEAP:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_CHILD_PEAK));
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_RETURN_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_CHILD));
                                        break;
                                }
                                break;
                        }
                        break;
                    case OFF_PEAK:
                        switch (getFareJourney()) {
                            case SINGLE:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_CHILD));
                                        break;
                                    case LEAP:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_SINGLE_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_CHILD_OFF_PEAK));
                                        break;
                                }
                                break;
                            case RETURN:
                                switch (getFarePayment()) {
                                    case CASH:
                                        cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_LUAS_RETURN_FARES, null);
                                        cursor.moveToFirst();

                                        switch (getLuasFareCost()) {
                                            case ONE_ZONE:
                                                cursor.moveToPosition(1);
                                                break;
                                            case TWO_ZONES:
                                                cursor.moveToPosition(2);
                                                break;
                                            case THREE_ZONES:
                                                cursor.moveToPosition(3);
                                                break;
                                            case FOUR_ZONES:
                                                cursor.moveToPosition(4);
                                                break;
                                            case FIVE_EIGHT_ZONES:
                                                cursor.moveToPosition(5);
                                                break;
                                        }
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_CHILD));
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
            case OTHER:
                break;
        }

        sqLiteDatabase.close();
        cursor.close();
    }

    /**
     * gets current time and checks whether or not it's peak time
     *
     * @return a boolean for if it's peak or not
     */
    public boolean isPeak() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();

        @SuppressLint("SimpleDateFormat")
        DateFormat date = new SimpleDateFormat("HH:mm");
        date.setTimeZone(TimeZone.getDefault());

        String localTime = date.format(currentLocalTime);
        System.out.println(localTime);

        int day = cal.get(Calendar.DAY_OF_WEEK);
        //xx:00
        String hour = localTime.substring(0, Math.min(localTime.length(), 2));
        //00:xx
        String minutes = localTime.substring(3, Math.min(localTime.length(), 5));
        int currentHour = Integer.parseInt(hour);
        System.out.println(currentHour);
        int currentMinutes = Integer.parseInt(minutes);
        System.out.println(currentMinutes);

        //monday through friday
        //7.45am to 9.30am peak
        //else off-peak
        if (day >= 2 && day <= 6) {
            if (currentHour == 7) {
                if (currentMinutes >= 45) {
                    return true;
                }
            } else if (currentHour == 8) {
                return true;
            } else if (currentHour == 9) {
                if (currentMinutes <= 30) {
                    return true;
                }
            }
        }
        return false;
    }

    public String formatDecimals(String fare) {
        double castFare = Double.parseDouble(fare);
        return String.format("%.2f", castFare);
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getFare() {
        return formatDecimals(fare);
    }

    public void setLuasFareCost(LuasFareCost luasFareCost) {
        this.luasFareCost = luasFareCost;
    }

    public LuasFareCost getLuasFareCost() {
        return luasFareCost;
    }

    public void setFareType(FareType fareType) {
        this.fareType = fareType;
    }

    public FareType getFareType() {
        return fareType;
    }

    public void setFareCaps(FareCaps fareCaps) {
        this.fareCaps = fareCaps;
    }

    public FareCaps getFareCaps() {
        return fareCaps;
    }

    public void setFarePayment(FarePayment farePayment) {
        this.farePayment = farePayment;
    }

    public FarePayment getFarePayment() {
        return farePayment;
    }

    public void setFareJourney(FareJourney fareJourney) {
        this.fareJourney = fareJourney;
    }

    public FareJourney getFareJourney() {
        return fareJourney;
    }

}
