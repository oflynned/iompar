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

/**
 * Created by ed on 29/10/15.
 */
public class Realtime extends Fragment {

    Globals globals = new Globals();

    View view;
    int stage = 0;

    private BaseAdapter baseAdapter;
    private GridView gridView;

    private enum TransportationCategories {LUAS, TRAIN, DART, BUS, BUS_EIREANN};
    private TransportationCategories currentCategory;

    Categories[] categories;
    Categories[] luasCategories;
    Categories[] greenLuasStations;
    Categories[] redLuasStations;

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

        luasCategories = new Categories[2];
        luasCategories[0] =
                new Categories("Green Line");
        luasCategories[1] =
                new Categories("Red Line");

        greenLuasStations = new Categories[globals.greenLineStations.length];
        for (int i = 0; i < globals.greenLineStations.length; i++) {

            greenLuasStations[i] = new Categories(globals.greenLineStations[i]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_realtime, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        baseAdapter = new TransportationAdapter(this.getContext());
        gridView.setAdapter(baseAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
           public void onItemClick(AdapterView<?> parent, View view, int position, long id){

               Categories[] currentChoice = null;

               if(stage == 0){
                   currentChoice = categories;
                   stage++;

                   switch(position){
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
                       default:
                           break;
                   }
                   gridView.setAdapter(baseAdapter);
               } else if (stage == 1){
                   /*
                      having chosen a transportation type, we need to show options within type
                      ie for luas: green line, red line...
                    */
                   if(currentCategory == TransportationCategories.LUAS){
                       currentChoice = luasCategories;
                       stage++;
                       gridView.setAdapter(baseAdapter);
                   }
               } else if (stage == 2){
                   /* Having chosen a line we we need to show stations for luas with respect to line chosen */
                   if(currentCategory == TransportationCategories.LUAS) {
                       currentChoice = greenLuasStations;
                       gridView.setAdapter(baseAdapter);
                   }
               }
           }
        });

        return view;
    }

    class TransportationAdapter extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater;

        public TransportationAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            int count = 0;

            if (stage == 0) {
                count = categories.length;
            } else if(stage == 1){
                if(currentCategory == TransportationCategories.LUAS){
                    count = luasCategories.length;
                }
            } else if(stage == 2){
                if(currentCategory == TransportationCategories.LUAS){
                    count = greenLuasStations.length;
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if(view == null){
                view = layoutInflater.inflate(R.layout.tile_layout, null);

                String title = "";
                TextView textView = (TextView) view.findViewById(R.id.tileTitle);

                switch(stage) {
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
                            title = greenLuasStations[position].getTitle();
                        }
                        break;
                    default:
                        break;
                }
                textView.setText(title);
            } else {
                TextView textView = (TextView) view.findViewById(R.id.tileTitle);

                switch(stage) {
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
                            textView.setText(greenLuasStations[position].getTitle());
                        }
                        break;
                    default:
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

