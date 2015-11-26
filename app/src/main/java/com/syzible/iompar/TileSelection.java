package com.syzible.iompar;

import android.content.Context;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * Created by ed on 20/11/15.
 */
public class TileSelection extends RelativeLayout implements Checkable {

    boolean checked;
    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    public TileSelection(Context context) {
        super(context);
    }

    @Override
    public void setChecked(boolean checked) {

    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void toggle() {

    }
}
