package com.uides.buyanywhere.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.uides.buyanywhere.R;
import com.uides.buyanywhere.ui.fragment.ProgressFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public abstract class LoadingActivity extends AppCompatActivity implements ProgressFragment.OnRetryListener {
    private ProgressFragment progressFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        progressFragment = getProgressFragment();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if(toolbar != null) {
            initToolBar(toolbar);
        }
    }

    protected int getLayoutID() {
        return R.layout.activity_blank;
    }

    protected void initToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getActivityTitle());
        }
    }

    protected String getActivityTitle() {
        return "";
    }

    public void showLoadingFragment() {
        showFragment(progressFragment);
    }

    public void showErrorScreen() {
        showLoadingFragment();
        progressFragment.showError(true);
    }

    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_root, fragment)
                .commit();
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
