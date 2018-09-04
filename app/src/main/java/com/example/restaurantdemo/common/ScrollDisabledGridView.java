package com.example.restaurantdemo.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ScrollDisabledGridView extends GridView {
    public ScrollDisabledGridView(Context context) {
        super(context);
    }

    public ScrollDisabledGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollDisabledGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }
        else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }

       super.onMeasure(widthMeasureSpec, heightSpec);
    }

//    private int mPosition;

//    public ScrollDisabledGridView(Context context) {
//        super(context);
//    }
//
//    public ScrollDisabledGridView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public ScrollDisabledGridView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;
//
//        if (actionMasked == MotionEvent.ACTION_DOWN) {
//            // Record the position the list the touch landed on
//            mPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
//            return super.dispatchTouchEvent(ev);
//        }
//
//        if (actionMasked == MotionEvent.ACTION_MOVE) {
//            // Ignore move events
//            return true;
//        }
//
//        if (actionMasked == MotionEvent.ACTION_UP) {
//            // Check if we are still within the same view
//            if (pointToPosition((int) ev.getX(), (int) ev.getY()) == mPosition) {
//                super.dispatchTouchEvent(ev);
//            } else {
//                // Clear pressed state, cancel the action
//                setPressed(false);
//                invalidate();
//                return true;
//            }
//        }
//
//        return super.dispatchTouchEvent(ev);
//    }
//
//
//    @Override
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
//                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
//        ViewGroup.LayoutParams params = getLayoutParams();
//        params.height = getMeasuredHeight();
//    }


}