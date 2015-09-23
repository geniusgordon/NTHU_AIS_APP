package com.example.gordon.nthuais.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by gordon on 9/23/15.
 */
public class ExtendedScrollView extends ScrollView {
    private OnScrollViewListener onScrollViewListener;

    public interface OnScrollViewListener {
        void onScrollChanged(ExtendedScrollView v, int l, int t, int oldl, int oldt);
    }

    public ExtendedScrollView(Context context) {
        super(context);
    }

    public ExtendedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnScrollViewListener(OnScrollViewListener listener) {
        onScrollViewListener = listener;
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        onScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        super.onScrollChanged(l, t, oldl, oldt);
    }

}
