package com.uides.buyanywhere.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.uides.buyanywhere.R;

/**
 * Created by TranThanhTung on 24/11/2017.
 */

public class ClearableEditText extends RelativeLayout implements View.OnClickListener {
    public static final String DEFAULT_ERROR_MESSAGE = "Thông tin bắt buộc";

    private boolean isRequired;
    private TextInputLayout textInputLayout;
    private EditText editText;

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ClearableEditText);

        isRequired = a.getBoolean(R.styleable.ClearableEditText_isRequired, false);

        Context context = getContext();
        textInputLayout = new TextInputLayout(context);
        LayoutParams textInputLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textInputLayout.setLayoutParams(textInputLp);
        textInputLayout.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);

        editText = new EditText(context);
        editText.setText(a.getString(R.styleable.ClearableEditText_android_text));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(R.styleable.ClearableEditText_android_textSize, 18));
        editText.setTextColor(a.getColor(R.styleable.ClearableEditText_android_textColor, Color.BLACK));
        editText.setHint(a.getString(R.styleable.ClearableEditText_android_hint));
        int lineNumber = a.getInt(R.styleable.ClearableEditText_android_lines, 1);
        editText.setLines(lineNumber);
        if(lineNumber == 1) {
            editText.setInputType(a.getInt(R.styleable.ClearableEditText_android_inputType, EditorInfo.TYPE_CLASS_TEXT));
        } else {
            editText.setInputType(a.getInt(R.styleable.ClearableEditText_android_inputType, EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE));
        }
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                textInputLayout.setError(null);
            }
        });
        LinearLayout.LayoutParams editTextLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(editTextLp);

        Button clearButton = new Button(context);
        clearButton.setBackground(a.getDrawable(R.styleable.ClearableEditText_clearDrawable));
        int clearButtonSize = 50;
        RelativeLayout.LayoutParams clearButtonLp = new RelativeLayout.LayoutParams(clearButtonSize, clearButtonSize);
        clearButtonLp.setMarginEnd(clearButtonSize / 3);
        clearButtonLp.addRule(RelativeLayout.ALIGN_PARENT_END);
        if (lineNumber == 1) {
            clearButtonLp.addRule(RelativeLayout.CENTER_VERTICAL);
        } else {
            clearButtonLp.setMargins(0, 8, 8, 0);
        }
        clearButton.setLayoutParams(clearButtonLp);
        clearButton.setOnClickListener(this);

        editText.setPadding(editText.getPaddingLeft(), editText.getPaddingTop(), clearButtonSize + 8, editText.getPaddingBottom());

        textInputLayout.addView(editText);
        addView(textInputLayout);
        addView(clearButton);
        a.recycle();
    }

    public boolean validate() {
        if (isRequired) {
            if (getText().isEmpty()) {
                textInputLayout.setError(DEFAULT_ERROR_MESSAGE);
                return false;
            } else {
                return true;
            }
        }
        textInputLayout.setError(null);
        return true;
    }

    public void setError(String text) {
        textInputLayout.setError(text);
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String value) {
        editText.setText(value);
    }

    @Override
    public void onClick(View v) {
        editText.setText("");
    }
}