package com.uides.buyanywhere.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uides.buyanywhere.R;

/**
 * Created by TranThanhTung on 22/11/2017.
 */

public abstract class LoadingFragment extends Fragment implements ProgressFragment.OnRetryListener {
    private ProgressFragment progressFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressFragment = getProgressFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getInflatedLayout(), container, false);
    }

    public void showLoadingFragment() {
        showFragment(progressFragment);
    }

    public void showErrorScreen() {
        showLoadingFragment();
        progressFragment.showError(true);
    }

    public void showFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(getRootLayout(), fragment)
                .commit();
    }

    protected int getInflatedLayout() {
        return R.layout.fragment_blank;
    }

    protected int getRootLayout() {
        return R.id.fl_root;
    }

    public ProgressFragment getProgressFragment() {
        ProgressFragment progressFragment = new ProgressFragment();
        progressFragment.setOnRetryListener(this);
        return progressFragment;
    }

    @Override
    public void onRetry() {
        progressFragment.showError(false);
    }
}
