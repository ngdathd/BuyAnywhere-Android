package com.uides.buyanywhere.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.model.Shop;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.shop.GetShopService;
import com.uides.buyanywhere.ui.fragment.shop.ShopPagerFragment;
import com.uides.buyanywhere.utils.SharedPreferencesOpenHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 27/11/2017.
 */

public class ShopActivity extends LoadingActivity {
    private static final String TAG = "PostProductActivity";
    private CompositeDisposable compositeDisposable;
    private GetShopService getShopService;

    private String shopID;
    private ShopPagerFragment shopPagerFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UserAuth.getAuthUser() == null) {
            UserAuth.setAuthUser(SharedPreferencesOpenHelper.getUser(this));
        }

        shopID = getIntent().getStringExtra(Constant.SHOP_ID);
        shopPagerFragment = new ShopPagerFragment();

        initServices();
        showLoadingFragment();
        fetchShops();
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        getShopService = Network.getInstance().createService(GetShopService.class);
    }

    private void fetchShops() {
        compositeDisposable.add(getShopService.getShop(UserAuth.getAuthUser().getAccessToken(),
                shopID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchShopSuccess, this::onFetchShopFailed));
    }

    private void onFetchShopSuccess(Shop shop) {
        showShopPagerFragment(shop);
    }

    public void showShopPagerFragment(Shop shop) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.SHOP, shop);
        bundle.putBoolean(Constant.ENABLE_REFRESHING, false);
        bundle.putInt(Constant.ACTIVE_TAB, getIntent().getIntExtra(Constant.ACTIVE_TAB, 0));
        bundle.putBoolean(Constant.IS_GUEST, true);
        bundle.putBoolean(Constant.IS_VIEW_BY_SHOP_OWNER, UserAuth.getAuthUser().getId().equals(shop.getOwnerId()));
        shopPagerFragment.setArguments(bundle);
        showFragment(shopPagerFragment);
    }

    private void onFetchShopFailed(Throwable e) {
        Log.i(TAG, "onFetchShopFailed: " + e);
        showErrorScreen();
    }

    @Override
    public void onRetry() {
        super.onRetry();
        fetchShops();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.shop);
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }
}
