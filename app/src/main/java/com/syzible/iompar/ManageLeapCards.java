package com.syzible.iompar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;

/**
 * Created by ed on 29/10/15.
 */
public class ManageLeapCards extends Fragment {

    View view;

    FilterMenu filterMenu;
    FilterMenuLayout filterMenuLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_manage_leap, null);

        filterMenuLayout = (FilterMenuLayout) view.findViewById(R.id.filter_menu);
        filterMenu = new FilterMenu.Builder(getActivity())
                .addItem(R.drawable.ic_action_add)
                .addItem(R.drawable.ic_action_clock)
                .addItem(R.drawable.ic_action_io)
                .addItem(R.drawable.ic_action_location_2)
                .attach(filterMenuLayout)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, int position) {
                        switch (position){
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                        }
                    }
                    @Override
                    public void onMenuCollapse() {

                    }
                    @Override
                    public void onMenuExpand() {

                    }
                })
                .build();

        return view;
    }
}
