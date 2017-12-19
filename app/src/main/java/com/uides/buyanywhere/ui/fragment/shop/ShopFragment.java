package com.uides.buyanywhere.ui.fragment.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.ui.activity.ShopRegisterActivity;
import com.uides.buyanywhere.ui.fragment.LoadingFragment;
import com.uides.buyanywhere.utils.SharedPreferencesOpenHelper;

/**
 * Created by TranThanhTung on 21/11/2017.
 */

public class ShopFragment extends LoadingFragment {
    private static final String TAG = "ShopFragment";
    private static final int SHOP_REGISTER_REQUEST_CODE = 0;

    private ChildShopFragment childShopFragment;
    private ShopUnregisteredFragment shopUnregisteredFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        childShopFragment = new ChildShopFragment();
        shopUnregisteredFragment = new ShopUnregisteredFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserAuth.getAuthUser().getShopID() == null) {
            if (!shopUnregisteredFragment.isAdded()) {
                showShopUnregisteredFragment();
            }
        } else {
            showFragment(childShopFragment);
        }
    }

    public void showShopUnregisteredFragment() {
        showFragment(new ShopUnregisteredFragment());
    }

    public void startRegisterActivity() {
        Intent intent = new Intent(getActivity(), ShopRegisterActivity.class);
        startActivityForResult(intent, SHOP_REGISTER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SHOP_REGISTER_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String shopID = bundle.getString(Constant.SHOP_ID);
                        UserAuth.getAuthUser().setShopID(shopID);

                        SharedPreferencesOpenHelper.saveUser(getActivity(), UserAuth.getAuthUser());
                    }
                }
            }
            break;

            default: {
                break;
            }
        }
    }
}
