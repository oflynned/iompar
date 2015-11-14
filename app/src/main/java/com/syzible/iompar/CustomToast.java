package com.syzible.iompar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ed on 11/11/15.
 */
public class CustomToast extends Toast {

    LayoutInflater inflater;
    TextView textView;
    View view;

    /**
     Constructor to provide a custom instantiation of a text view in which a custom toast is shown
     @param context provides the context of the instantiation for the current activity
     */
    public CustomToast(Context context){
        super(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_toast, (ViewGroup) view.findViewById(R.id.custom_toast_layout));
        setView(view);

        textView = (TextView) view.findViewById(R.id.toast_text);
        textView.setText("Next tram is x mins away");
    }
}
