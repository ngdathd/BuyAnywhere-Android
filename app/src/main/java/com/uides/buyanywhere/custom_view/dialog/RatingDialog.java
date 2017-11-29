package com.uides.buyanywhere.custom_view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.hedgehog.ratingbar.RatingBar;
import com.uides.buyanywhere.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 21/11/2017.
 */

public class RatingDialog implements RatingBar.OnRatingChangeListener, DialogInterface.OnShowListener {
    @BindView(R.id.rating_bar)
    RatingBar ratingBar;
    @BindView(R.id.txt_level)
    TextView textLevel;
    @BindView(R.id.edt_feedback)
    EditText textFeedback;

    private AlertDialog alertDialog;
    private int rating = 0;
    private OnPositiveButtonClickListener onPositiveButtonClickListener;
    private View.OnClickListener onDialogPositiveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(rating == 0) {
                YoYo.with(Techniques.Shake)
                        .duration(800)
                        .playOn(ratingBar);
                textLevel.setText(R.string.rating_required);
                return;
            }

            if (onPositiveButtonClickListener != null) {
                if (onPositiveButtonClickListener.onPositiveButtonClick(rating, textFeedback.getText().toString())) {
                    alertDialog.dismiss();
                }
            }
        }
    };

    public RatingDialog(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_rating, null, false);
        ButterKnife.bind(this, rootView);
        ratingBar.setOnRatingChangeListener(this);

        alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.rating_dialog_title)
                .setView(rootView)
                .setPositiveButton(R.string.send_feedback, null)
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(true)
                .create();
    }

    public RatingDialog setOnPositiveButtonClickListener(OnPositiveButtonClickListener onPositiveButtonClickListener) {
        this.onPositiveButtonClickListener = onPositiveButtonClickListener;
        return this;
    }

    public void show() {
        alertDialog.setOnShowListener(this);
        alertDialog.show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }

    @Override
    public void onRatingChange(float RatingCount) {
        rating = (int) RatingCount;
        switch (rating) {
            case 1: {
                textLevel.setText(R.string.very_unpleased);
            }
            break;

            case 2: {
                textLevel.setText(R.string.unpleased);
            }
            break;

            case 3:{
                textLevel.setText(R.string.common);
            }
            break;

            case 4:{
                textLevel.setText(R.string.good);
            }
            break;

            case 5: {
                textLevel.setText(R.string.very_pleased);
            }
            break;

            default:{
                break;
            }
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        Button buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonPositive.setOnClickListener(onDialogPositiveButtonClickListener);
    }

    public interface OnPositiveButtonClickListener {
        boolean onPositiveButtonClick(int rating, String textFeedback);
    }
}
