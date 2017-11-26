package com.uides.buyanywhere.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.model.UserProfile;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.user.GetFavoriteCategoriesService;
import com.uides.buyanywhere.service.user.GetUserProfileService;
import com.uides.buyanywhere.ui.fragment.LoadingFragment;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 22/11/2017.
 */

public class ProfileFragment extends LoadingFragment {
    public static final String TAG = "ProfileFragment";
    private ProfileChildFragment profileChildFragment;
    private CompositeDisposable compositeDisposable;
    private GetUserProfileService getUserProfileService;
    private GetFavoriteCategoriesService getFavoriteCategoriesService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initService();
        fetchUserInfoAndFavoriteCategories();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initService() {
        compositeDisposable = new CompositeDisposable();
        getUserProfileService = Network.getInstance().createService(GetUserProfileService.class);
        getFavoriteCategoriesService = Network.getInstance().createService(GetFavoriteCategoriesService.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (profileChildFragment == null) {
            showLoadingFragment();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    public void fetchUserInfoAndFavoriteCategories() {
        String accessToken = UserAuth.getAuthUser().getAccessToken();

        Observable<UserProfile> observableUserProfile = getUserProfileService
                .getUserProfile(accessToken);

        Observable<List<Category>> observableFavoriteCategories = getFavoriteCategoriesService
                .getFavoriteCategories(accessToken);

        Disposable disposable = Observable.combineLatest(observableUserProfile, observableFavoriteCategories, (userProfile, categories) -> {
            userProfile.setFavoriteCategories(categories);
            return userProfile;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onFetchDataSuccess, this::onFetchDataFailed);
        compositeDisposable.add(disposable);
    }

    private void onFetchDataSuccess(UserProfile userProfile) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.USER_PROFILE, userProfile);
        profileChildFragment = new ProfileChildFragment();
        profileChildFragment.setArguments(bundle);

        showFragment(profileChildFragment);
    }

    private void onFetchDataFailed(Throwable e) {
        Log.i(TAG, "onFetchDataFailed: " + e);
        showErrorScreen();
    }

    @Override
    public void onRetry() {
        super.onRetry();
        fetchUserInfoAndFavoriteCategories();
    }
}
