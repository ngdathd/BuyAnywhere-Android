package com.uides.buyanywhere.custom_view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class StrikeThroughPriceTextView extends PriceTextView {
    public StrikeThroughPriceTextView(Context context) {
        super(context);
        init();
    }

    public StrikeThroughPriceTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StrikeThroughPriceTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }
}
