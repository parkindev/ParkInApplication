package com.example.thanyapat.parkinapplication;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by Thanyapat on 3/23/2016 AD.
 */
public class CustomScroll extends ScrollView {

    public CustomScroll(Context context) {
        super(context);
    }

    public CustomScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return true;
    }
    @Override
    public ArrayList<View> getFocusables(int direction) {
        return new ArrayList<View>();
    }

}
