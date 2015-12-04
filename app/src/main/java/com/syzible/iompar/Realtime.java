package com.syzible.iompar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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

    TextView leftPanel, rightPanel;

    private boolean start, end, pair = false;
    String startPosition, endPosition = "";
    int startPositionComp, endPositionComp, choice;

    View view;
    int stage = 0;

    private BaseAdapter baseAdapter;
    private GridView gridView;

    private enum TransportationCategories {LUAS, TRAIN, DART, BUS, BUS_EIREANN}

    private enum LuasLines {GREEN, RED}

    private enum LuasDirections {
        TALLAGHT, SAGGART, POINT,
        BRIDES_GLEN, SANDYFORD, STEPHENS_GREEN,
        CONNOLLY, HEUSTON
    }

    Globals.LineDirection lineDirection;

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

    Sync sync = new Sync();
    Globals globals = new Globals();

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
                gridView.invalidate();
            }
        }
    };

    /**
     * Assigns strings to their appropriate categories within their subcategories
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this.getContext())
                .registerReceiver(onBackPressedBroadcastReceiver,
                        new IntentFilter(MainActivity.ON_BACK_PRESSED_EVENT));

        //assign transportation types to adapter
        categories = new Categories[5];
        categories[0] =
                new Categories("Luas", "local");
        categories[1] =
                new Categories("Dublin Bus", "local");
        categories[2] =
                new Categories("DART", "local");
        categories[3] =
                new Categories("Train", "regional");
        categories[4] =
                new Categories("Bus Éireann", "regional");

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
                new Categories("Saggart-The Point Line");
        //fuck my life and Veolia's ridiculous planning to have 2 stations 50m apart on a sub route
        luasDirectionRed[2] =
                new Categories("Heuston-Connolly Line");

        //luas stations
        greenLuasStationsBridesGlen = new Categories[globals.greenLineStationsBridesGlenStephensGreen.length];
        for (int i = 0; i < globals.greenLineStationsBridesGlenStephensGreen.length; i++) {
            greenLuasStationsBridesGlen[i] = new Categories(globals.greenLineStationsBridesGlenStephensGreen[i]);
        }

        greenLuasStationsSandyford = new Categories[globals.greenLineBeforeSandyford.length];
        for (int i = 0; i < globals.greenLineBeforeSandyford.length; i++) {
            greenLuasStationsSandyford[i] = new Categories(globals.greenLineStationsSandyfordStephensGreen[i]);
        }

        redLuasStationsTallaght = new Categories[globals.redLineStationsTallaghtPoint.length];
        for (int i = 0; i < globals.redLineStationsTallaghtPoint.length; i++) {
            redLuasStationsTallaght[i] = new Categories(globals.redLineStationsTallaghtPoint[i]);
        }

        redLuasStationsSaggart = new Categories[globals.redLineStationsSaggartPoint.length];
        for (int i = 0; i < globals.redLineStationsSaggartPoint.length; i++) {
            redLuasStationsSaggart[i] = new Categories(globals.redLineStationsSaggartPoint[i]);
        }

        redLuasStationsConnolly = new Categories[globals.redLineStationsHeustonConnolly.length];
        for (int i = 0; i < globals.redLineStationsHeustonConnolly.length; i++) {
            redLuasStationsConnolly[i] = new Categories(globals.redLineStationsHeustonConnolly[i]);
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

        infoPanelParams.height = 0;

        leftPanel = (TextView) view.findViewById(R.id.leftpanel);
        rightPanel = (TextView) view.findViewById(R.id.rightpanel);

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

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
                        case 1:
                            //train
                            setCurrentCategory(TransportationCategories.TRAIN);
                            break;
                        case 2:
                            //DART
                            setCurrentCategory(TransportationCategories.DART);
                            break;
                        case 3:
                            //dublin bus
                            setCurrentCategory(TransportationCategories.BUS);
                            break;
                        case 4:
                            //bus eireann
                            setCurrentCategory(TransportationCategories.BUS_EIREANN);
                            break;
                    }
                    gridView.setAdapter(baseAdapter);
                } else if (stage == 1) {
                   /*
                      Having chosen a transportation type, we need to show options within type
                      ie for luas: green line, red line...
                    */
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
                   /* Having chosen a type we we need to show line choosable, ie stations to tallaght, saggart... */
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
                                case 3:
                                    currentLuasDirection = LuasDirections.CONNOLLY;
                                    stage++;
                                    gridView.setAdapter(baseAdapter);
                            }
                        }
                    }
                } else if (stage == 3) {
                   /* Having chosen a type we we need to show line choosable, ie stations to tallaght, saggart... */
                    if (currentCategory == TransportationCategories.LUAS) {
                        if (currentLuasLine == LuasLines.GREEN) {
                            if (currentLuasDirection == LuasDirections.BRIDES_GLEN) {
                                //RTPI Luas station parsing & syncing
                                currentChoice = greenLuasStationsBridesGlen;

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

                                            fetchRTPI(getStartPosition(), getEndPosition(),
                                                    getDirection(currentLuasLine, getStartPositionComp(), getEndPositionComp()));
                                            infoPanelParams.height = getDp(100);
                                            infoPanel.invalidate();
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
                            } else if (currentLuasDirection == LuasDirections.SANDYFORD) {
                                currentChoice = greenLuasStationsSandyford;
                                //fetchRTPI(currentChoice, position, Globals.LineDirection.stephens_green_to_sandyford);
                            }
                        } else {
                            if (currentLuasDirection == LuasDirections.TALLAGHT) {
                                currentChoice = redLuasStationsTallaght;
                                //fetchRTPI(currentChoice, position, Globals.LineDirection.the_point_to_tallaght);
                            } else if (currentLuasDirection == LuasDirections.SAGGART) {
                                currentChoice = redLuasStationsSaggart;
                                //fetchRTPI(currentChoice, position, Globals.LineDirection.the_point_to_saggart);
                            } else if (currentLuasDirection == LuasDirections.CONNOLLY) {
                                currentChoice = redLuasStationsConnolly;
                                //fetchRTPI(currentChoice, position, Globals.LineDirection.heuston_to_connolly);
                            }
                        }
                    }
                }
            }
        });

        return view;
    }

    public void setCurrentChoice(int choice) {
        this.choice = choice;
    }

    public int getCurrentChoice() {
        return choice;
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
    public Globals.LineDirection getDirection(LuasLines currentLine, int startPosition, int endPosition) {
        if (currentLine == LuasLines.GREEN) {
            //stephen's green - sandyford
            //sandyford - bride's glen
            //stephen's green - bride's glen

            //bride's glen - stephen's green
            //sandyford - stephen's green
            if (endPosition > startPosition) {
                return lineDirection = Globals.LineDirection.stephens_green_to_brides_glen;
            } else if (endPosition < startPosition) {
                return lineDirection = Globals.LineDirection.brides_glen_to_stephens_green;
            } else {
                return null;
            }
        }
        //point - tallaght
        //tallaght - point
        //point - saggart
        //saggart - point
        //saggart - belgard
        //belgard - saggart
        //heuston - connolly
        //connolly - heuston
        //red cow diversions?
        else if (currentLine == LuasLines.RED) {
            if (endPosition > startPosition) {
                return lineDirection = Globals.LineDirection.the_point_to_tallaght;
            } else {
                return lineDirection = Globals.LineDirection.tallaght_to_the_point;
            }
        } else {
            System.out.println("entered else loop -- check conditions");
            return null;
        }
    }

    public static boolean getZone(String station, String[] items) {
        for (String item : items) {
            if (station.contains(item)) {
                return true;
            }
        }
        return false;
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
        try {
            sync.requestUpdate(lineDirection, depart, arrive);
            ensureDataArrival();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * Causes the current thread to sleep for duration n if and only if the data has not been
     * fully loaded into the string
     *
     * @fixes issues associated with null toasts
     */
    private void ensureDataArrival() {
        while (!sync.isLoaded()) {
            try {
                Thread.sleep(1);
                if (sync.isLoaded()) {
                    sync.setLoaded(true);
                    leftPanel.setText(sync.getNextDue());
                    rightPanel.setText(sync.getArrivalInfo());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
}

