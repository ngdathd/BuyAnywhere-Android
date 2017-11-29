package com.uides.buyanywhere.custom_view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.storage.UploadTask;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.utils.FirebaseUploadImageHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public class TwoModeProgressDialog extends Dialog implements FirebaseUploadImageHelper.OnProgressListener, FirebaseUploadImageHelper.OnNextTaskListener {
    @BindView(R.id.fl_indeterminate)
    RelativeLayout layoutIndeterminate;

    @BindView(R.id.fl_determinate)
    RelativeLayout layoutDeterminate;
    @BindView(R.id.txt_current)
    TextView textCurrent;
    @BindView(R.id.txt_total)
    TextView textTotal;
    @BindView(R.id.txt_percent)
    TextView textPercent;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    public TwoModeProgressDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.two_mode_progress_dialog);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public void switchToIndeterminateMode() {
        layoutIndeterminate.setVisibility(View.VISIBLE);
        layoutDeterminate.setVisibility(View.INVISIBLE);
    }

    public void switchToDeterminateMode() {
        layoutDeterminate.setVisibility(View.VISIBLE);
        layoutIndeterminate.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        int progress = (int) (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
        textPercent.setText("" + progress);
        progressBar.setProgress(progress);
    }

    @Override
    public void onNextTask(int index, int total) {
        textCurrent.setText("" + (index + 1));
        textTotal.setText("" + total);
    }
}
