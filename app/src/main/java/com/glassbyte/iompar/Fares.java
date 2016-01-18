package com.glassbyte.iompar;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ed on 29/10/15.
 */
public class Fares extends Fragment {

    int originId, destinationId;
    String direction, fare, endStation, type;
    boolean isLeap = true;

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
    Globals globals;
    Sync sync;

    private boolean start, end, pair = false;
    String startPosition, endPosition = "";
    int startPositionComp, endPositionComp;

    View view;
    int stage = 0;

    private BaseAdapter baseAdapter;
    private GridView gridView;
    Switch leapSwitch, cashSwitch;
    TextView cost, zones;

    public enum TransportationCategories {LUAS, TRAIN, DART, BUS, BUS_EIREANN}

    public enum LuasLines {GREEN, RED}

    public enum LuasDirections {
        TALLAGHT, SAGGART, CONNOLLY,
        BRIDES_GLEN, SANDYFORD
    }

    private TransportationCategories currentCategory;
    private LuasLines currentLuasLine;
    private LuasDirections currentLuasDirection;

    //current choice
    Categories[] categories;
    Categories[] currentChoice;
    //luas
    Categories[] luasCategories;
    Categories[] luasDirectionGreen;
    Categories[] luasDirectionRed;
    Categories[] greenLuasStationsBridesGlen;
    Categories[] greenLuasStationsSandyford;
    Categories[] redLuasStationsTallaght;
    Categories[] redLuasStationsSaggart;
    Categories[] redLuasStationsConnolly;

    private BroadcastReceiver onBackPressedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (stage > 0) {
                stage--;
                switch (stage) {
                    case 0:
                        gridView.setAdapter(new TransportationAdapter(context));
                        break;
                    default:
                        if (currentCategory == TransportationCategories.LUAS)
                            gridView.setAdapter(new TransportationAdapter(context));
                        else if (currentCategory == TransportationCategories.TRAIN)
                            gridView.setAdapter(new TransportationAdapter(context));
                        else if (currentCategory == TransportationCategories.DART)
                            gridView.setAdapter(new TransportationAdapter(context));
                        else if (currentCategory == TransportationCategories.BUS)
                            gridView.setAdapter(new TransportationAdapter(context));
                        else if (currentCategory == TransportationCategories.BUS_EIREANN)
                            gridView.setAdapter(new TransportationAdapter(context));
                }
                gridView.setItemChecked(getStartPositionComp(), false);
                gridView.setItemChecked(getEndPositionComp(), false);
                setStart(false);
                setEnd(false);
                setHasPair(false);
                gridView.invalidate();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this.getContext())
                .registerReceiver(onBackPressedBroadcastReceiver,
                        new IntentFilter(MainActivity.ON_BACK_PRESSED_EVENT));
        globals = new Globals(getContext());
        sync = new Sync(getContext());

        categories = new Categories[1];
        categories[0] = new Categories(getString(R.string.luas_title), "local");

        //luas line types
        luasCategories = new Categories[2];
        luasCategories[0] =
                new Categories(getString(R.string.green_line));
        luasCategories[1] =
                new Categories(getString(R.string.red_line));

        //sublines within luas lines
        luasDirectionGreen = new Categories[2];
        luasDirectionGreen[0] =
                new Categories(getString(R.string.brides_glen_stephens_green));
        luasDirectionGreen[1] =
                new Categories(getString(R.string.sandyford_stephens_green));

        luasDirectionRed = new Categories[3];
        luasDirectionRed[0] =
                new Categories(getString(R.string.tallaght_point));
        luasDirectionRed[1] =
                new Categories(getString(R.string.saggart_connolly));
        //fuck my life and Veolia's ridiculous planning to have 2 stations 50m apart on a sub route
        luasDirectionRed[2] =
                new Categories(getString(R.string.heuston_connolly));

        //luas stations
        greenLuasStationsBridesGlen = new Categories[globals.greenLineStationsBridesGlenStephensGreen.length];
        for (int i = 0; i < globals.greenLineStationsBridesGlenStephensGreen.length; i++) {
            greenLuasStationsBridesGlen[i] = new Categories(globals.greenLineStationsBridesGlenStephensGreen[i]);
        }

        greenLuasStationsSandyford = new Categories[globals.greenLineBeforeSandyford.length];
        for (int i = 0; i < globals.greenLineBeforeSandyford.length; i++) {
            greenLuasStationsSandyford[i] = new Categories(globals.greenLineBeforeSandyford[i]);
        }

        redLuasStationsTallaght = new Categories[globals.redLineStationsTallaghtPoint.length];
        for (int i = 0; i < globals.redLineStationsTallaghtPoint.length; i++) {
            redLuasStationsTallaght[i] = new Categories(globals.redLineStationsTallaghtPoint[i]);
        }

        redLuasStationsSaggart = new Categories[globals.redLineStationsSaggartConnolly.length];
        for (int i = 0; i < globals.redLineStationsSaggartConnolly.length; i++) {
            redLuasStationsSaggart[i] = new Categories(globals.redLineStationsSaggartConnolly[i]);
        }

        redLuasStationsConnolly = new Categories[globals.redLineStationsHeustonConnolly.length];
        for (int i = 0; i < globals.redLineStationsHeustonConnolly.length; i++) {
            redLuasStationsConnolly[i] = new Categories(globals.redLineStationsHeustonConnolly[i]);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fare, null);
        gridView = (GridView) view.findViewById(R.id.gridview);
        baseAdapter = new TransportationAdapter(this.getContext());
        gridView.setAdapter(baseAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        leapSwitch = (Switch) view.findViewById(R.id.leap_switch);
        cashSwitch = (Switch) view.findViewById(R.id.cash_switch);
        cost = (TextView) view.findViewById(R.id.figure_text);
        zones = (TextView) view.findViewById(R.id.zones_text);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (stage == 0) {
                    currentChoice = categories;
                    stage++;

                    switch (position) {
                        case 0:
                            //luas
                            setCurrentCategory(TransportationCategories.LUAS);
                            break;
                    }
                    gridView.setAdapter(baseAdapter);
                } else if (stage == 1) {
                    if (getCurrentCategory() == TransportationCategories.LUAS) {
                        switch (position) {
                            case 0:
                                currentLuasLine = LuasLines.GREEN;
                                stage++;
                                gridView.setAdapter(baseAdapter);
                                break;
                            case 1:
                                currentLuasLine = LuasLines.RED;
                                stage++;
                                gridView.setAdapter(baseAdapter);
                                break;
                        }
                    }
                } else if (stage == 2) {
                    if (getCurrentCategory() == TransportationCategories.LUAS) {
                        if (currentLuasLine == LuasLines.GREEN) {
                            switch (position) {
                                case 0:
                                    currentLuasDirection = LuasDirections.BRIDES_GLEN;
                                    stage++;
                                    gridView.setAdapter(baseAdapter);
                                    break;
                                case 1:
                                    currentLuasDirection = LuasDirections.SANDYFORD;
                                    stage++;
                                    gridView.setAdapter(baseAdapter);
                                    break;
                            }
                        } else {
                            switch (position) {
                                case 0:
                                    currentLuasDirection = LuasDirections.TALLAGHT;
                                    stage++;
                                    gridView.setAdapter(baseAdapter);
                                    break;
                                case 1:
                                    currentLuasDirection = LuasDirections.SAGGART;
                                    stage++;
                                    gridView.setAdapter(baseAdapter);
                                    break;
                                case 2:
                                    currentLuasDirection = LuasDirections.CONNOLLY;
                                    stage++;
                                    gridView.setAdapter(baseAdapter);
                            }
                        }
                    }
                } else if (stage == 3) {
                    if (currentCategory == TransportationCategories.LUAS) {
                        if (currentLuasLine == LuasLines.GREEN) {
                            if (currentLuasDirection == LuasDirections.BRIDES_GLEN) {
                                currentChoice = greenLuasStationsBridesGlen;
                            } else if (currentLuasDirection == LuasDirections.SANDYFORD) {
                                currentChoice = greenLuasStationsSandyford;
                            }
                        } else {
                            if (currentLuasDirection == LuasDirections.TALLAGHT) {
                                currentChoice = redLuasStationsTallaght;
                            } else if (currentLuasDirection == LuasDirections.SAGGART) {
                                currentChoice = redLuasStationsSaggart;
                            } else if (currentLuasDirection == LuasDirections.CONNOLLY) {
                                currentChoice = redLuasStationsConnolly;
                            }
                        }
                        if (!Arrays.toString(currentChoice).equals("")) {
                            handleChoices(currentChoice, position);
                        }
                    }
                }
            }
        });

        leapSwitch.setChecked(true);
        cashSwitch.setChecked(false);

        leapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (leapSwitch.isChecked()) {
                    cashSwitch.setChecked(false);
                    isLeap = true;
                } else {
                    cashSwitch.setChecked(true);
                    isLeap = false;
                }

                if (isLeap)
                    type = "leap";
                else
                    type = "cash";

                if (hasPair()) {
                    cost.setText("€" + getZoneTraversal(sync.convertStringToEnum(getChosenEndStation()),
                            getStartPosition(), getEndPosition(), getContext(), type));
                    cost.invalidate();
                }
            }
        });

        cashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cashSwitch.isChecked()) {
                    leapSwitch.setChecked(false);
                    isLeap = false;
                } else {
                    leapSwitch.setChecked(true);
                    isLeap = true;
                }

                if (isLeap)
                    type = "leap";
                else
                    type = "cash";

                if (hasPair()) {
                    cost.setText("€" + getZoneTraversal(sync.convertStringToEnum(getChosenEndStation()),
                            getStartPosition(), getEndPosition(), getContext(), type));
                    cost.invalidate();
                }
            }
        });

        return view;
    }

    private void handleChoices(Categories[] currentChoice, int position) {
        //check loop
        if (gridView.isItemChecked(position)) {
            if (!isStart() && !isEnd()) {
                System.out.println("start and end were false, now start set true with end still false");
                setStartPositionComp(position);
                setStart(true);
                setStartPosition(currentChoice[position].getTitle());
            } else if (isStart() && !isEnd()) {
                if (position != getStartPositionComp()) {
                    System.out.println("end was false where start is true, now end set true");
                    setEndPositionComp(position);
                    setEnd(true);
                    setHasPair(true);
                    setEndPosition(currentChoice[position].getTitle());
                } else {
                    System.out.println("start was true, now start set false");
                    setStart(false);
                    setStartPosition("");
                }
            } else if (!isStart() && isEnd()) {
                if (position != getStartPositionComp()) {
                    System.out.println("start was false where end is true, now start set true");
                    setStartPositionComp(position);
                    setStart(true);
                    setHasPair(true);
                    setStartPosition(currentChoice[position].getTitle());
                } else {
                    System.out.println("start was false, now start set true");
                    setStart(false);
                    setStartPosition("");
                }
            }
        }
        //uncheck loop
        else {
            if (!isStart() && !isEnd()) {
                System.out.println("start and end were false, now start set true with end still false");
                setStartPositionComp(position);
                setStart(true);
                setStartPosition(currentChoice[position].getTitle());
            } else if (isStart() && !isEnd()) {
                if (position != getStartPositionComp()) {
                    System.out.println("end was false where start is true, now end set true");
                    setEndPositionComp(position);
                    setHasPair(true);
                    setEnd(true);
                    setEndPosition(currentChoice[position].getTitle());
                } else {
                    System.out.println("start was true, now start set false");
                    setStart(false);
                    setEndPosition("");
                }
            } else if (!isStart() && isEnd()) {
                if (position != getEndPositionComp()) {
                    System.out.println("start was false where end is true, now start set true");
                    setStartPositionComp(position);
                    setStart(true);
                    setHasPair(true);
                    setStartPosition(currentChoice[position].getTitle());
                } else {
                    System.out.println("end was true, now end set false");
                    setEnd(false);
                    setEndPosition("");
                }
            } else if (isStart() && isEnd()) {
                if (position == getStartPositionComp()) {
                    System.out.println("start and end were true in a pair, now start set false");
                    setStart(false);
                    setHasPair(false);
                    setStartPosition("");
                } else if (position == getEndPositionComp()) {
                    System.out.println("start and end were true in a pair, now end set false");
                    setEnd(false);
                    setHasPair(false);
                    setEndPosition("");
                }
            }
        }
        if (isStart() && isEnd()) {
            if (hasPair()) {
                if (position != getStartPositionComp()
                        && position != getEndPositionComp()) {
                    gridView.setItemChecked(position, false);
                    System.out.println("trying to check item not already checked");
                } else {
                    System.out.println("start true, end true!");
                    System.out.println(
                            "start station: " + getStartPosition() + "\n" +
                                    "end station: " + getEndPosition());

                    if (currentLuasDirection == LuasDirections.SAGGART) {
                        if (getStartPositionComp() < getEndPositionComp()) {
                            setEndStation(Globals.LineDirection.connolly_to_saggart);
                        } else {
                            setEndStation(Globals.LineDirection.belgard_to_saggart);
                        }
                    } else if (currentLuasDirection == LuasDirections.TALLAGHT) {
                        if (getStartPositionComp() < getEndPositionComp()) {
                            setEndStation(Globals.LineDirection.the_point_to_tallaght);
                        } else {
                            setEndStation(Globals.LineDirection.tallaght_to_the_point);
                        }
                    } else if (currentLuasDirection == LuasDirections.CONNOLLY) {
                        if (getStartPositionComp() < getEndPositionComp()) {
                            setEndStation(Globals.LineDirection.connolly_to_saggart);
                        } else {
                            setEndStation(Globals.LineDirection.belgard_to_saggart);
                        }
                    } else if (currentLuasDirection == LuasDirections.SANDYFORD) {
                        if (getStartPositionComp() < getEndPositionComp()) {
                            setEndStation(Globals.LineDirection.stephens_green_to_sandyford);
                        } else {
                            setEndStation(Globals.LineDirection.sandyford_to_stephens_green);
                        }
                    } else if (currentLuasDirection == LuasDirections.BRIDES_GLEN) {
                        if (getStartPositionComp() < getEndPositionComp()) {
                            setEndStation(Globals.LineDirection.stephens_green_to_brides_glen);
                        } else {
                            setEndStation(Globals.LineDirection.brides_glen_to_stephens_green);
                        }
                    }

                    if (isLeap)
                        type = "leap";
                    else
                        type = "cash";

                    cost.setText("€" + getZoneTraversal(sync.convertStringToEnum(getChosenEndStation()),
                            getStartPosition(), getEndPosition(), getContext(), type));

                    int difference, origin, destination;

                    if (getChosenEndStation().equals(getString(R.string.the_point)) ||
                            getChosenEndStation().equals(getString(R.string.tallaght))) {
                        //tallaght-point
                        origin = getTallaghtZoneId(getOriginId(), getDestinationId());
                        destination = getTallaghtZoneId(getDestinationId(), getOriginId());
                        difference = (origin - destination);
                        if (difference < 0) {
                            difference = difference * -1;
                        }
                        difference++;
                        zones.setText("" + difference);
                        zones.invalidate();
                    } else if (getChosenEndStation().equals(getString(R.string.connolly)) ||
                            getChosenEndStation().equals(getString(R.string.saggart)) ||
                            getChosenEndStation().equals(getString(R.string.heuston))) {
                        origin = getSaggartZoneId(getOriginId(), getDestinationId());
                        destination = getSaggartZoneId(getDestinationId(), getOriginId());
                        difference = (origin - destination);
                        if (difference < 0) {
                            difference = difference * -1;
                        }
                        difference++;
                        zones.setText("" + difference);
                        zones.invalidate();
                    } else if (getChosenEndStation().equals(getString(R.string.stephens_green)) ||
                            getChosenEndStation().equals(getString(R.string.sandyford)) ||
                            getChosenEndStation().equals(getString(R.string.brides_glen))) {
                        origin = getGreenLineZoneId(getOriginId(), getDestinationId());
                        destination = getGreenLineZoneId(getDestinationId(), getOriginId());
                        difference = (origin - destination);
                        if (difference < 0) {
                            difference = difference * -1;
                        }
                        difference++;
                        zones.setText("" + difference);
                        zones.invalidate();
                    }
                }

        } else {
            if (position == getEndPositionComp()) {
                setEnd(false);
                System.out.println("start true, end true, unselected end so end is false!");
            } else if (position == getStartPositionComp()) {
                setStart(false);
                System.out.println("start true, end true, unselected start so start is false!");
            }
            setHasPair(false);
        }
    }
    System.out.println("Start pos: " + getStartPosition() + ", end pos: " + getEndPosition());
    baseAdapter.notifyDataSetChanged();
    baseAdapter.notifyDataSetInvalidated();
}

    public String getChosenEndStation() {
        return endStation;
    }

    public void setEndStation(Globals.LineDirection direction) {
        //green line
        if (direction.equals(Globals.LineDirection.stephens_green_to_brides_glen) ||
                direction.equals(Globals.LineDirection.stephens_green_to_sandyford)) {
            this.endStation = getString(R.string.brides_glen);
        } else if (direction.equals(Globals.LineDirection.sandyford_to_stephens_green) ||
                direction.equals(Globals.LineDirection.brides_glen_to_stephens_green)) {
            this.endStation = getString(R.string.stephens_green);
        }

        //red line - tallaght/point
        else if (direction.equals(Globals.LineDirection.belgard_to_tallaght) ||
                direction.equals(Globals.LineDirection.the_point_to_tallaght)) {
            this.endStation = getString(R.string.tallaght);
        } else if (direction.equals(Globals.LineDirection.belgard_to_the_point) ||
                direction.equals(Globals.LineDirection.tallaght_to_the_point) ||
                direction.equals(Globals.LineDirection.tallaght_to_belgard)) {
            this.endStation = getString(R.string.the_point);
        } else if (direction.equals(Globals.LineDirection.the_point_to_belgard)) {
            this.endStation = getString(R.string.tallaght);
        }

        //saggart/connolly
        else if (direction.equals(Globals.LineDirection.belgard_to_saggart)) {
            this.endStation = getString(R.string.saggart);
        } else if (direction.equals(Globals.LineDirection.saggart_to_belgard)) {
            this.endStation = getString(R.string.the_point);
        } else if (direction.equals(Globals.LineDirection.belgard_to_connolly)) {
            this.endStation = getString(R.string.connolly);
        } else if (direction.equals(Globals.LineDirection.connolly_to_belgard)) {
            this.endStation = getString(R.string.saggart);
        }

        //connolly/heuston
        else if (direction.equals(Globals.LineDirection.heuston_to_connolly)) {
            this.endStation = getString(R.string.connolly);
        } else if (direction.equals(Globals.LineDirection.connolly_to_heuston)) {
            this.endStation = getString(R.string.saggart);
        }
    }

    /**
     * takes the parameters of type of fare for the user, and returns the appropriate costs
     *
     * @param line        the Luas line being traversed
     * @param origin      departing station
     * @param destination destination station
     * @return the cost of given transit route in Euro
     */
    public String getZoneTraversal(Realtime.LuasDirections line, String origin, String destination,
                                   Context context, String paymentType) {
        //always single for individual journeys chosen INSIDE realtime frag
        setFareJourney(FareJourney.SINGLE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fareType = sharedPreferences.getString(context.getResources().getString(R.string.pref_key_fare), "");

        //retrieve fare type from intro/settings class under key pref
        if (fareType.equals(context.getResources().getString(R.string.adult))) {
            setFareType(FareType.ADULT);
        } else if (fareType.equals(context.getResources().getString(R.string.student))) {
            setFareType(FareType.STUDENT);
        } else if (fareType.equals(context.getResources().getString(R.string.child))) {
            setFareType(FareType.CHILD);
        } else if (fareType.equals(context.getResources().getString(R.string.other))) {
            setFareType(FareType.OTHER);
        } else {
            setFareType(FareType.ADULT);
        }

        switch (paymentType) {
            case "cash":
                setFarePayment(FarePayment.CASH);
                break;
            case "leap":
                setFarePayment(FarePayment.LEAP);
                break;
            default:
                //retrieve if set payment method is cash or leap
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(DatabaseHelper.SELECT_ALL_ACTIVE_LEAP_CARDS, null);
                if (cursor.getCount() > 0) {
                    setFarePayment(FarePayment.LEAP);
                } else {
                    setFarePayment(FarePayment.CASH);
                }
                databaseHelper.close();
                sqLiteDatabase.close();
                cursor.close();
                break;
        }

        System.out.println("Using payment method: " + getFarePayment());

        //peak?
        if (isPeak()) {
            setFareCaps(FareCaps.ON_PEAK);
            System.out.println("It is currently peak hour");
        } else {
            setFareCaps(FareCaps.OFF_PEAK);
            System.out.println("It is currently NOT peak hour");
        }

        setOriginId(getStationIndex(line, origin, context));
        setDestinationId(getStationIndex(line, destination, context));
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
    public int getStationIndex(Realtime.LuasDirections line, String station, Context context) {
        Globals globals = new Globals(context);
        System.out.println("LINE: " + line);
        int index = 0;
        switch (line) {
            case TALLAGHT:
                for (String searchStation : globals.redLineStationsTallaghtPoint) {
                    index++;
                    if (searchStation.contains(station)) {
                        return index;
                    }
                }
                break;
            case SAGGART:
            case CONNOLLY:
                for (String searchStation : globals.redLineStationsSaggartConnolly) {
                    index++;
                    if (searchStation.contains(station)) {
                        return index;
                    }
                }
                break;
            case POINT:
                for (String searchStation : globals.redLineStationsTallaghtPoint) {
                    index++;
                    if (searchStation.contains(station)) {
                        return index;
                    }
                }
                break;
            case BRIDES_GLEN:
            case SANDYFORD:
            case STEPHENS_GREEN:
                for (String searchStation : globals.greenLineStationsBridesGlenStephensGreen) {
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
                                        cursor.close();
                                        break;
                                    case LEAP:
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT_STUDENT_PEAK));
                                        cursor.close();
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_ADULT));
                                        cursor.close();
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
                                        cursor.close();
                                        break;
                                    case LEAP:
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT_STUDENT_OFF_PEAK));
                                        cursor.close();
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_ADULT));
                                        cursor.close();
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
                                        cursor.close();
                                        break;
                                    case LEAP:
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT_STUDENT_PEAK));
                                        cursor.close();
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_ADULT));
                                        cursor.close();
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
                                        cursor.close();
                                        break;
                                    case LEAP:
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_ADULT_STUDENT_OFF_PEAK));
                                        cursor.close();
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_ADULT));
                                        cursor.close();
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_CHILD));
                                        cursor.close();
                                        break;
                                    case LEAP:
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_CHILD_PEAK));
                                        cursor.close();
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_CHILD));
                                        cursor.close();
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_CHILD));
                                        cursor.close();
                                        break;
                                    case LEAP:
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_SINGLE_FARES_CHILD_OFF_PEAK));
                                        cursor.close();
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
                                        setFare(cursor.getString(DatabaseHelper.COL_LUAS_RETURN_FARES_CHILD));
                                        cursor.close();
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
            case OTHER:
                //assuming elderly & disabled get free transport?
                setFare(String.valueOf(0));
                break;
        }

        databaseHelper.close();
        sqLiteDatabase.close();
        if (cursor != null)
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
        int day = cal.get(Calendar.DAY_OF_WEEK);
        //xx:00
        String hour = localTime.substring(0, Math.min(localTime.length(), 2));
        //00:xx
        String minutes = localTime.substring(3, Math.min(localTime.length(), 5));
        int currentHour = Integer.parseInt(hour);
        int currentMinutes = Integer.parseInt(minutes);

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

    public static double formatDecimals(String fare) {
        System.out.println("1 being called");
        double castFare;

        DecimalFormat decimalFormat = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.UK));
        castFare = Double.parseDouble(fare.replaceAll("[€]", ""));
        System.out.println(decimalFormat.format(castFare));

        return castFar



        e;
    }

    public static String formatDecimals(double fare) {
        System.out.println("2 being called");
        return String.format("%.2f", fare).replaceAll(",", ".");
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


    public void setStartPosition(String startPosition) {
        this.startPosition = startPosition;
    }

    public String getStartPosition() {
        return startPosition;
    }

    public void setEndPosition(String endPosition) {
        this.endPosition = endPosition;
    }

    public String getEndPosition() {
        return endPosition;
    }

    public void setStartPositionComp(int startPositionComp) {
        this.startPositionComp = startPositionComp;
    }

    public int getStartPositionComp() {
        return startPositionComp;
    }

    public void setEndPositionComp(int endPositionComp) {
        this.endPositionComp = endPositionComp;
    }

    public int getEndPositionComp() {
        return endPositionComp;
    }

    public void setHasPair(boolean pair) {
        this.pair = pair;
    }

    public boolean hasPair() {
        return pair;
    }

    public void setCurrentCategory(TransportationCategories currentCategory) {
        this.currentCategory = currentCategory;
    }

    public TransportationCategories getCurrentCategory() {
        return currentCategory;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isStart() {
        return start;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isEnd() {
        return end;
    }

class TransportationAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;

    /**
     * Default constructor for the transportation adapter which passes the current context
     * and instantiates an appropriate layout contextually
     *
     * @param context the current application context
     */
    public TransportationAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * Counts the number of static elements within the enumeration's state array and adds it
     * to the count for the current state which the view returns
     *
     * @return the count of items within the array to be displayed
     */
    @Override
    public int getCount() {
        int count = 0;

        if (stage == 0) {
            count = categories.length;
        } else if (stage == 1) {
            if (currentCategory == TransportationCategories.LUAS) {
                count = luasCategories.length;
            }
        } else if (stage == 2) {
            if (currentCategory == TransportationCategories.LUAS) {
                if (currentLuasLine == LuasLines.GREEN) {
                    count = luasDirectionGreen.length;
                } else {
                    count = luasDirectionRed.length;
                }
            }
        } else if (stage == 3) {
            if (currentCategory == TransportationCategories.LUAS) {
                if (currentLuasLine == LuasLines.GREEN) {
                    if (currentLuasDirection == LuasDirections.BRIDES_GLEN) {
                        count = greenLuasStationsBridesGlen.length;
                    } else if (currentLuasDirection == LuasDirections.SANDYFORD) {
                        count = greenLuasStationsSandyford.length;
                    }
                } else if (currentLuasLine == LuasLines.RED) {
                    if (currentLuasDirection == LuasDirections.TALLAGHT) {
                        count = redLuasStationsTallaght.length;
                    } else if (currentLuasDirection == LuasDirections.SAGGART) {
                        count = redLuasStationsSaggart.length;
                    } else if (currentLuasDirection == LuasDirections.CONNOLLY) {
                        count = redLuasStationsConnolly.length;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Returns the current stage advanced or returned as a set of arguments within enumeration states
     *
     * @return view returns the current contextual view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.tile_layout, null);

            String title = "";
            TextView textView = (TextView) view.findViewById(R.id.tileTitle);

            switch (stage) {
                case 0:
                    title = categories[position].getTitle();
                    break;
                case 1:
                    if (currentCategory == TransportationCategories.LUAS) {
                        title = luasCategories[position].getTitle();
                    }
                    break;
                case 2:
                    if (currentCategory == TransportationCategories.LUAS) {
                        if (currentLuasLine == LuasLines.GREEN) {
                            title = luasDirectionGreen[position].getTitle();
                        } else {
                            title = luasDirectionRed[position].getTitle();
                        }
                    }
                    break;
                case 3:
                    if (currentCategory == TransportationCategories.LUAS) {
                        if (currentLuasLine == LuasLines.GREEN) {
                            if (currentLuasDirection == LuasDirections.BRIDES_GLEN) {
                                title = greenLuasStationsBridesGlen[position].getTitle();
                            } else if (currentLuasDirection == LuasDirections.SANDYFORD) {
                                title = greenLuasStationsSandyford[position].getTitle();
                            }
                        } else {
                            if (currentLuasDirection == LuasDirections.TALLAGHT) {
                                title = redLuasStationsTallaght[position].getTitle();
                            } else if (currentLuasDirection == LuasDirections.SAGGART) {
                                title = redLuasStationsSaggart[position].getTitle();
                            } else if (currentLuasDirection == LuasDirections.CONNOLLY) {
                                title = redLuasStationsConnolly[position].getTitle();
                            }
                        }
                    }
                    break;
            }
            textView.setText(title);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.tileTitle);

            switch (stage) {
                case 0:
                    textView.setText(categories[position].getTitle());
                    break;
                case 1:
                    if (currentCategory == TransportationCategories.LUAS) {
                        textView.setText(luasCategories[position].getTitle());
                    }
                    break;
                case 2:
                    if (currentCategory == TransportationCategories.LUAS) {
                        if (currentLuasLine == LuasLines.GREEN) {
                            textView.setText(luasDirectionGreen[position].getTitle());
                        } else {
                            textView.setText(luasDirectionRed[position].getTitle());
                        }
                    }
                    break;
                case 3:
                    if (currentCategory == TransportationCategories.LUAS) {
                        if (currentLuasLine == LuasLines.GREEN) {
                            if (currentLuasDirection == LuasDirections.BRIDES_GLEN) {
                                textView.setText(greenLuasStationsBridesGlen[position].getTitle());
                            } else if (currentLuasDirection == LuasDirections.SANDYFORD) {
                                textView.setText(greenLuasStationsSandyford[position].getTitle());
                            }
                        } else {
                            if (currentLuasDirection == LuasDirections.TALLAGHT) {
                                textView.setText(redLuasStationsTallaght[position].getTitle());
                            } else if (currentLuasDirection == LuasDirections.SAGGART) {
                                textView.setText(redLuasStationsSaggart[position].getTitle());
                            } else if (currentLuasDirection == LuasDirections.CONNOLLY) {
                                textView.setText(redLuasStationsConnolly[position].getTitle());
                            }
                        }
                    }
                    break;
            }
        }
        return view;
    }
}

class Categories {
    private String title;
    private String type;

    //constructors
    public Categories(String title, String type) {
        this.title = title;
        this.type = type;
    }

    public Categories(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
}
