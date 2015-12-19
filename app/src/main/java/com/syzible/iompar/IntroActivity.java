package com.syzible.iompar;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;

/**
 * Created by ed on 06/12/15.
 */
public class IntroActivity extends AppIntro2 {

    Slide_One slide_one;
    Slide_Two slide_two;
    Slide_Three slide_three;
    Slide_Four slide_four;

    @Override
    public void init(Bundle bundle) {
        //set fullscreen effect
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        //fragment objects
        slide_one = new Slide_One();
        slide_two = new Slide_Two();
        slide_three = new Slide_Three();
        slide_four = new Slide_Four();

        //add slides
        addSlide(slide_one);
        addSlide(slide_two);
        addSlide(slide_three);
        addSlide(slide_four);

        //transition animation
        setFlowAnimation();
    }

    @Override
    public void onDonePressed() {
        Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNextPressed() {
        slide_two.title.setText(getString(R.string.slide_two_title));
        slide_two.desc.setText(getString(R.string.slide_two_description));
        slide_two.nameField.setHint(getString(R.string.slide_two_hint));

        slide_two.title.invalidate();
        slide_two.desc.invalidate();
        slide_two.nameField.invalidate();
    }

    @Override
    public void onSlideChanged() {

    }
}
