package com.uides.buyanywhere.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.uides.buyanywhere.R;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 22/11/2017.
 */

public class ProgressFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.ln_error)
    LinearLayout errorLayout;
    @BindView(R.id.av_loading_indicator)
    AVLoadingIndicatorView avLoadingIndicatorView;

    private OnRetryListener onRetryListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loading, container, false);
        ButterKnife.bind(this, rootView);
        rootView.findViewById(R.id.btn_retry).setOnClickListener(this);
        return rootView;
    }

    public void showError(boolean isShow) {
        errorLayout.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        avLoadingIndicatorView.setVisibility(isShow? View.INVISIBLE : View.VISIBLE);
    }

    public OnRetryListener getOnRetryListener() {
        return onRetryListener;
    }

    public void setOnRetryListener(OnRetryListener onRetryListener) {
        this.onRetryListener = onRetryListener;
    }

    @Override
    public void onClick(View v) {
        if(onRetryListener != null) {
            onRetryListener.onRetry();
        }
    }

    public interface OnRetryListener {
        void onRetry();
    }
}
