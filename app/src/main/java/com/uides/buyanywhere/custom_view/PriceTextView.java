package com.uides.buyanywhere.custom_view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by TranThanhTung on 20/11/2017.
 */

public class PriceTextView extends AppCompatTextView {
    public PriceTextView(Context context) {
        super(context);
        init();
    }

    public PriceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PriceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    public void setPrice(String price, String unit) {
        setText(reformatPrice(price, unit));
    }

    public static String reformatPrice(String price, String unit) {
        StringBuilder newPrice = new StringBuilder(price);
        int priceLength = price.length() - 1;
        for (int i = priceLength - 2; i > 0; i -= 3) {
            newPrice.insert(i, ".");
        }
        return newPrice.append(unit).toString();
    }
}
