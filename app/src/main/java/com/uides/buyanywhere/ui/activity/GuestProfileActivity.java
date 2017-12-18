package com.uides.buyanywhere.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.model.UserProfile;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.user.GetUserService;
import com.uides.buyanywhere.ui.fragment.profile.GuestProfileChildFragment;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 18/12/2017.
 */

public class GuestProfileActivity extends LoadingActivity{
    private static final String TAG = "GuestProfileActivity";
    private CompositeDisposable compositeDisposable;
    private GetUserService getUserService;

    private String userID;
    private GuestProfileChildFragment guestProfileChildFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getIntent().getStringExtra(Constant.USER_ID);
        guestProfileChildFragment = new GuestProfileChildFragment();

        initServices();
        showLoadingFragment();
        fetchUser();
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        getUserService = Network.getInstance().createService(GetUserService.class);
    }

    private void fetchUser() {
        compositeDisposable.add(getUserService.getUserProfile(userID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchShopSuccess, this::onFetchShopFailed));
    }

    private void onFetchShopSuccess(UserProfile userProfile) {
        showGuestProfileChildFragment(userProfile);
    }

    public void showGuestProfileChildFragment(UserProfile userProfile) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.USER_PROFILE, userProfile);
        guestProfileChildFragment.setArguments(bundle);
        showFragment(guestProfileChildFragment);
    }

    private void onFetchShopFailed(Throwable e) {
        Log.i(TAG, "onFetchShopFailed: " + e);
        showErrorScreen();
    }
    @Override
    public void onRetry() {
        super.onRetry();
        fetchUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }
}
