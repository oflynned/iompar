package com.syzible.iompar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by ed on 29/10/15.
 */
public class Realtime extends Fragment {

    RelativeLayout infoPanel;
    RelativeLayout.LayoutParams infoPanelParams;
    SwipeRefreshLayout swipeRefreshLayout;

    AsynchronousActivity asynchronousActivity;

    TextView leftPanel, rightPanel;

    private boolean start, end, pair = false;
    String startPosition, endPosition = "";
    int startPositionComp, endPositionComp;

    View view;
    int stage = 0;

    private BaseAdapter baseAdapter;
    private GridView gridView;

    public enum TransportationCategories {LUAS, TRAIN, DART, BUS, BUS_EIREANN}

    public enum LuasLines {GREEN, RED}

    public enum LuasDirections {
        TALLAGHT, SAGGART, POINT, BELGARD,
        BRIDES_GLEN, SANDYFORD, STEPHENS_GREEN,
        CONNOLLY, HEUSTON
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
    //train
    Categories[] trainCategories;
    Categories[] trainDirection;
    Categories[] trainStations;
    //dart
    Categories[] dartCategories;
    Categories[] dartDirection;
    Categories[] dartStations;
    //dublin bus
    Categories[] dbLine;
    Categories[] dbDirection;
    Categories[] dbStop;
    //bus eireann
    Categories[] beCategories;
    Categories[] beDirection;
    Categories[] beStations;

    Sync sync;

    /**
     * Overrides the onBackPress() and returns to previous stage without closing fragment
     * and invalidates the gridView to redraw the current stage
     */
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
                infoPanelParams.height = getDp(0);
                infoPanel.invalidate();
            }
        }
    };

    /**
     * Assigns strings to their appropriate categories within their subcategories
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        sync = new Sync(this.getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this.getContext())
                .registerReceiver(onBackPressedBroadcastReceiver,
                        new IntentFilter(MainActivity.ON_BACK_PRESSED_EVENT));

        //assign transportation types to adapter
        categories = new Categories[1];
        categories[0] = new Categories("Luas", "local");

        /* rest will be implemented soon as fares and tracking get finished
        categories = new Categories[5];
        categories[0] =
                new Categories("Bus Éireann", "regional");
        categories[1] =
                new Categories("DART", "local");
        categories[2] =
                new Categories("Dublin Bus", "local");
        categories[3] =
                new Categories("Luas", "local");
        categories[4] =
                new Categories("Train", "regional");*/

        //luas line types
        luasCategories = new Categories[2];
        luasCategories[0] =
                new Categories("Green Line");
        luasCategories[1] =
                new Categories("Red Line");

        //sublines within luas lines
        luasDirectionGreen = new Categories[2];
        luasDirectionGreen[0] =
                new Categories("Bride's Glen-St. Stephen's Green Line");
        luasDirectionGreen[1] =
                new Categories("Sandyford-St. Stephen's Green Line");

        luasDirectionRed = new Categories[3];
        luasDirectionRed[0] =
                new Categories("Tallaght-The Point Line");
        luasDirectionRed[1] =
                new Categories("Saggart-Connolly Line");
        //fuck my life and Veolia's ridiculous planning to have 2 stations 50m apart on a sub route
        luasDirectionRed[2] =
                new Categories("Heuston-Connolly Line");

        //luas stations
        greenLuasStationsBridesGlen = new Categories[Globals.greenLineStationsBridesGlenStephensGreen.length];
        for (int i = 0; i < Globals.greenLineStationsBridesGlenStephensGreen.length; i++) {
            greenLuasStationsBridesGlen[i] = new Categories(Globals.greenLineStationsBridesGlenStephensGreen[i]);
        }

        greenLuasStationsSandyford = new Categories[Globals.greenLineBeforeSandyford.length];
        for (int i = 0; i < Globals.greenLineBeforeSandyford.length; i++) {
            greenLuasStationsSandyford[i] = new Categories(Globals.greenLineStationsSandyfordStephensGreen[i]);
        }

        redLuasStationsTallaght = new Categories[Globals.redLineStationsTallaghtPoint.length];
        for (int i = 0; i < Globals.redLineStationsTallaghtPoint.length; i++) {
            redLuasStationsTallaght[i] = new Categories(Globals.redLineStationsTallaghtPoint[i]);
        }

        redLuasStationsSaggart = new Categories[Globals.redLineStationsSaggartConnolly.length];
        for (int i = 0; i < Globals.redLineStationsSaggartConnolly.length; i++) {
            redLuasStationsSaggart[i] = new Categories(Globals.redLineStationsSaggartConnolly[i]);
        }

        redLuasStationsConnolly = new Categories[Globals.redLineStationsHeustonConnolly.length];
        for (int i = 0; i < Globals.redLineStationsHeustonConnolly.length; i++) {
            redLuasStationsConnolly[i] = new Categories(Globals.redLineStationsHeustonConnolly[i]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_realtime, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        baseAdapter = new TransportationAdapter(this.getContext());
        gridView.setAdapter(baseAdapter);

        infoPanel = (RelativeLayout) view.findViewById(R.id.infopanel);
        infoPanelParams = (RelativeLayout.LayoutParams) infoPanel.getLayoutParams();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRTPI(getStartPosition(), getEndPosition(),
                        getDirection(currentLuasLine, currentLuasDirection, getStartPositionComp(), getEndPositionComp()));
            }
        });

        infoPanelParams.height = 0;
        infoPanel.requestLayout();

        leftPanel = (TextView) view.findViewById(R.id.leftpanel);
        rightPanel = (TextView) view.findViewById(R.id.rightpanel);

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (stage == 0) {
                    currentChoice = categories;
                    stage++;

                    switch (position) {
                        /*case 0:
                            //bus eireann
                            setCurrentCategory(TransportationCategories.BUS_EIREANN);
                            break;
                        case 1:
                            //DART
                            setCurrentCategory(TransportationCategories.DART);
                            break;
                        case 2:
                            //dublin bus
                            setCurrentCategory(TransportationCategories.BUS);
                            break;
                        case 3:
                            //luas
                            setCurrentCategory(TransportationCategories.LUAS);
                            break;
                        case 4:
                            //train
                            setCurrentCategory(TransportationCategories.TRAIN);
                            break;
                            */
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
                    System.out.println("CURRENT DIRECTION " + getDirection(currentLuasLine, currentLuasDirection, getStartPositionComp(), getEndPositionComp()));

                    fetchRTPI(getStartPosition(), getEndPosition(),
                            getDirection(currentLuasLine, currentLuasDirection, getStartPositionComp(), getEndPositionComp()));
                    infoPanelParams.height = getDp(90);
                    infoPanel.invalidate();
                    infoPanel.requestLayout();
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

    public void setHasPair(boolean pair) {
        this.pair = pair;
    }

    public boolean hasPair() {
        return pair;
    }

    /**
     * returns the appropriate direction the user must traverse via sync in order to
     * make an appropriate journey and scrape the appropriate end station
     *
     * @param currentLine   current luas line the user is travelling on
     * @param startPosition the station which the user is departing from
     * @param endPosition   station where the user is travelling to
     * @return the line direction from globals which is passed as a param to sync
     */
    public Globals.LineDirection getDirection(LuasLines currentLine, LuasDirections currentLuasDirection,
                                              int startPosition, int endPosition) {
        if (currentLine == LuasLines.GREEN) {
            if (startPosition < endPosition) {
                if (startPosition <= Globals.SANDYFORD_ID && endPosition <= Globals.SANDYFORD_ID) {
                    return Globals.LineDirection.stephens_green_to_sandyford;
                } else if (endPosition > Globals.SANDYFORD_ID) {
                    return Globals.LineDirection.stephens_green_to_brides_glen;
                }
            } else {
                if (startPosition > Globals.SANDYFORD_ID) {
                    return Globals.LineDirection.brides_glen_to_stephens_green;
                } else {
                    return Globals.LineDirection.sandyford_to_stephens_green;
                }
            }
        } else if (currentLine == LuasLines.RED) {
            if (currentLuasDirection == LuasDirections.TALLAGHT) {
                if (endPosition > startPosition) {
                    if (endPosition <= Globals.BELGARD_TALLAGHT_ID - 1 &&
                            startPosition < Globals.BELGARD_TALLAGHT_ID - 1) {
                        return Globals.LineDirection.the_point_to_belgard;
                    } else if (startPosition >= Globals.BELGARD_TALLAGHT_ID - 1 &&
                            endPosition > Globals.BELGARD_TALLAGHT_ID - 1 &&
                            endPosition <= Globals.TALLAGHT_ID) {
                        return Globals.LineDirection.belgard_to_tallaght;
                    } else if (startPosition < Globals.BELGARD_TALLAGHT_ID - 1 &&
                            endPosition > Globals.BELGARD_TALLAGHT_ID - 1) {
                        return Globals.LineDirection.the_point_to_tallaght;
                    }
                } else {
                    if (startPosition <= Globals.TALLAGHT_ID - 1 &&
                            startPosition > Globals.BELGARD_TALLAGHT_ID - 1 &&
                            endPosition >= Globals.BELGARD_TALLAGHT_ID - 1) {
                        return Globals.LineDirection.tallaght_to_belgard;
                    } else if (startPosition <= Globals.BELGARD_TALLAGHT_ID - 1 &&
                            endPosition < Globals.BELGARD_TALLAGHT_ID - 1 &&
                            endPosition >= Globals.THE_POINT_TALLAGHT_ID - 1) {
                        return Globals.LineDirection.belgard_to_the_point;
                    } else if (startPosition > Globals.BELGARD_TALLAGHT_ID - 1 &&
                            startPosition <= Globals.TALLAGHT_ID - 1 &&
                            endPosition < Globals.BELGARD_TALLAGHT_ID - 1) {
                        return Globals.LineDirection.tallaght_to_the_point;
                    }
                }
            } else if (currentLuasDirection == LuasDirections.SAGGART) {
                if (endPosition > startPosition) {
                    if (endPosition <= Globals.BELGARD_SAGGART_ID - 1 &&
                            startPosition < Globals.BELGARD_SAGGART_ID - 1) {
                        return Globals.LineDirection.connolly_to_belgard;
                    } else if (startPosition >= Globals.BELGARD_SAGGART_ID - 1 &&
                            endPosition > Globals.BELGARD_SAGGART_ID - 1 &&
                            endPosition <= Globals.SAGGART_ID - 1) {
                        return Globals.LineDirection.belgard_to_saggart;
                    } else if (startPosition < Globals.BELGARD_SAGGART_ID - 1 &&
                            endPosition > Globals.BELGARD_SAGGART_ID - 1) {
                        return Globals.LineDirection.connolly_to_saggart;
                    }
                } else {
                    if (startPosition <= Globals.SAGGART_ID - 1 &&
                            startPosition > Globals.BELGARD_SAGGART_ID - 1 &&
                            endPosition >= Globals.BELGARD_SAGGART_ID - 1) {
                        return Globals.LineDirection.saggart_to_belgard;
                    } else if (startPosition <= Globals.BELGARD_SAGGART_ID - 1 &&
                            endPosition < Globals.BELGARD_SAGGART_ID - 1 &&
                            endPosition >= Globals.CONNOLLY_SAGGART_ID - 1) {
                        return Globals.LineDirection.belgard_to_connolly;
                    } else if (startPosition > Globals.BELGARD_SAGGART_ID - 1 &&
                            startPosition <= Globals.SAGGART_ID - 1 &&
                            endPosition < Globals.BELGARD_SAGGART_ID - 1) {
                        return Globals.LineDirection.saggart_to_belgard;
                    }
                }
            } else if (currentLuasDirection == LuasDirections.CONNOLLY) {
                if (startPosition > endPosition) {
                    return Globals.LineDirection.heuston_to_connolly;
                } else {
                    return Globals.LineDirection.connolly_to_heuston;
                }
            }
        }
        return null;
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

    public int getDp(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixels, getContext().getResources().getDisplayMetrics());
    }

    /**
     * Fetches the appropriate RTPI data given the parameters from RTPI.ie
     *
     * @param lineDirection the direction in which the user is travelling
     */
    private void fetchRTPI(String depart, String arrive, Globals.LineDirection lineDirection) {
        //RTPI Luas station parsing & syncing
        asynchronousActivity = new AsynchronousActivity(depart, arrive, lineDirection);
        asynchronousActivity.execute();
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

    /**
     * displays appropriate data polled asynchronously to the upper panel of the realtime fragment
     * @error during thread switching outside of asynchronously
     */
    private void displayRTPI(String leftPanelText, String rightPanelText) {
        leftPanel.setText(leftPanelText);
        rightPanel.setText(rightPanelText);
    }

    class TransportationAdapter extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater;

        /**
         * Default constructor for the transportation adapter which passes the current context
         * and instantiates an appropriate layout contextually
         * @param context the current application context
         */
        public TransportationAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        /**
         * Counts the number of static elements within the enumeration's state array and adds it
         * to the count for the current state which the view returns
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
         * @return view    returns the current contextual view
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

    class AsynchronousActivity extends AsyncTask<String, Void, String> {

        Globals.LineDirection lineDirection;
        String depart, arrive;

        public AsynchronousActivity(String depart, String arrive, Globals.LineDirection lineDirection){
            this.lineDirection = lineDirection;
            this.depart = depart;
            this.arrive = arrive;
        }

        @Override
        protected  void onPreExecute() {
            baseAdapter.notifyDataSetChanged();
            infoPanel.invalidate();
            displayRTPI("Loading...", "Loading...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                sync.requestUpdate(lineDirection, depart, arrive);
                sync.setLoaded(false);
                while (!sync.isLoaded()) {
                    try {
                        synchronized (this) {
                            Thread.sleep(1);
                            if (sync.isLoaded()) {
                                sync.setLoaded(true);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                displayRTPI("Error in retrieving data!", "Please check your internet connection!");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            baseAdapter.notifyDataSetChanged();
            infoPanel.invalidate();
            displayRTPI(sync.getNextDue(), sync.getArrivalInfo());

            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}

