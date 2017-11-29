package com.uides.buyanywhere.ui.fragment.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.model.Shop;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.user.GetOwnerShop;
import com.uides.buyanywhere.ui.fragment.LoadingFragment;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public class ChildShopFragment extends LoadingFragment {
    private static final String TAG = "ChildShopFragment";
    private ShopPagerFragment shopPagerFragment;
    private CompositeDisposable compositeDisposable;

    private Shop shop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopPagerFragment = new ShopPagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (shop == null) {
            fetchShopInformation();
        } else {
            if(!shopPagerFragment.isAdded()) {
                showShopPagerFragment(shop);
            }
        }
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void fetchShopInformation() {
        showLoadingFragment();
        this.compositeDisposable = new CompositeDisposable();
        GetOwnerShop getOwnerShop = Network.getInstance().createService(GetOwnerShop.class);
        compositeDisposable.add(getOwnerShop.getOwnerShop(UserAuth.getAuthUser().getAccessToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchShopInformationSuccess, this::onFetchShopInformationFailed));
    }

    private void onFetchShopInformationSuccess(Shop shop) {
        this.shop = shop;
        showShopPagerFragment(shop);
    }

    public void onShopUpdated(Shop shop) {
        this.shop = shop;
    }

    public void showShopPagerFragment(Shop shop) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.SHOP, shop);
        bundle.putBoolean(Constant.IS_VIEW_BY_SHOP_OWNER, true);
        shopPagerFragment.setArguments(bundle);
        showFragment(shopPagerFragment);
    }

    private void onFetchShopInformationFailed(Throwable e) {
        Log.i(TAG, "onFetchShopInformationFailed: " + e);
        showErrorScreen();
    }

    @Override
    public void onRetry() {
        super.onRetry();
        fetchShopInformation();
    }
}
