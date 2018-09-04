package com.example.restaurantdemo.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScrollDisableRecyclerView extends RecyclerView {


    public ScrollDisableRecyclerView(Context context) {
        super(context);
    }

    public ScrollDisableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollDisableRecyclerView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        //Ignore scroll events.
        if (ev.getAction() == MotionEvent.ACTION_MOVE)
            return true;

        //Dispatch event for non-scroll actions, namely clicks!
        return super.dispatchTouchEvent(ev);
    }
}
