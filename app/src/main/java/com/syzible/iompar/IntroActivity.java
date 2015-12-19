package com.syzible.iompar;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;

/**
 * Created by ed on 06/12/15.
 */
public class IntroActivity extends AppIntro2 {

    @Override
    public void init(Bundle bundle) {
        //set fullscreen effect
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        //fragment objects
        Slide_One slide_one = new Slide_One();
        Slide_Two slide_two = new Slide_Two();
        Slide_Three slide_three = new Slide_Three();
        Slide_Four slide_four = new Slide_Four();

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

    }

    @Override
    public void onSlideChanged() {

    }
}
