package com.syzible.iompar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ed on 29/10/15.
 */
public class Realtime extends Fragment {

    Globals globals = new Globals();

    View view;
    int stage = 0;

    private BaseAdapter baseAdapter;
    private GridView gridView;

    private enum TransportationCategories {LUAS, TRAIN, DART, BUS, BUS_EIREANN}
    private enum LuasLines {GREEN, RED}
    private enum LuasDirections {TALLAGHT, SAGGART, BRIDES_GLEN, SANDYFORD}

    private TransportationCategories currentCategory;
    private LuasLines currentLuasLine;
    private LuasDirections currentLuasDirection;

    Categories[] categories;
    Categories[] luasCategories;
    Categories[] luasDirectionGreen;
    Categories[] luasDirectionRed;
    Categories[] greenLuasStationsBridesGlen;
    Categories[] greenLuasStationsSandyford;
    Categories[] redLuasStationsTallaght;
    Categories[] redLuasStationsSaggart;

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
                new Categories("Bride's Glen Line");
        luasDirectionGreen[1] =
                new Categories("Sandyford Line");

        luasDirectionRed = new Categories[2];
        luasDirectionRed[0] =
                new Categories("Tallaght Line");
        luasDirectionRed[1] =
                new Categories("Saggart Line");

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_realtime, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        baseAdapter = new TransportationAdapter(this.getContext());
        gridView.setAdapter(baseAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Categories[] currentChoice = null;

                if (stage == 0) {
                    currentChoice = categories;
                    stage++;

                    switch (position) {
                        case 0:
                            //luas
                            currentCategory = TransportationCategories.LUAS;
                            break;
                        case 1:
                            //train
                            currentCategory = TransportationCategories.TRAIN;
                            break;
                        case 2:
                            //DART
                            currentCategory = TransportationCategories.DART;
                            break;
                        case 3:
                            //dublin bus
                            currentCategory = TransportationCategories.BUS;
                            break;
                        case 4:
                            //bus eireann
                            currentCategory = TransportationCategories.BUS_EIREANN;
                            break;
                    }
                    Toast.makeText(getContext(), "Category chosen " + currentCategory, Toast.LENGTH_SHORT).show();
                    gridView.setAdapter(baseAdapter);
                } else if (stage == 1) {
                   /*
                      Having chosen a transportation type, we need to show options within type
                      ie for luas: green line, red line...
                    */
                    if (currentCategory == TransportationCategories.LUAS) {
                        switch (position){
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
                        Toast.makeText(getContext(), "Line chosen " + currentLuasLine, Toast.LENGTH_SHORT).show();
                    }
                } else if (stage == 2) {
                   /* Having chosen a type we we need to show line choosable, ie stations to tallaght, saggart... */
                    if (currentCategory == TransportationCategories.LUAS) {
                        if (currentLuasLine == LuasLines.GREEN) {
                            switch(position){
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
                            switch(position){
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
                            }
                        }
                        Toast.makeText(getContext(), "Direction chosen " + currentLuasDirection, Toast.LENGTH_SHORT).show();
                    }
                } else if (stage == 3) {
                   /* Having chosen a type we we need to show line choosable, ie stations to tallaght, saggart... */
                    if (currentCategory == TransportationCategories.LUAS) {
                        if (currentLuasLine == LuasLines.GREEN) {
                            if (currentLuasDirection == LuasDirections.BRIDES_GLEN) {
                                gridView.setAdapter(baseAdapter);
                            } else if (currentLuasDirection == LuasDirections.SANDYFORD) {
                                gridView.setAdapter(baseAdapter);
                            }
                        } else {
                            if (currentLuasDirection == LuasDirections.TALLAGHT) {
                                gridView.setAdapter(baseAdapter);
                            } else if (currentLuasDirection == LuasDirections.SAGGART) {
                                gridView.setAdapter(baseAdapter);
                            }
                        }
                    }
                }
            }
        });

        return view;
    }

    class TransportationAdapter extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater;

        /**
         * Default constructor for the transportation adapter which passes the current context
         * and instantiates an appropriate layout contextually
         * @param context   the current application context
         */
        public TransportationAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        /**
         *  Counts the number of static elements within the enumeration's state array and adds it
         *  to the count for the current state which the view returns
         *  @return count   the count of items within the array to be displayed
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
         * @return    view    returns the current contextual view
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

