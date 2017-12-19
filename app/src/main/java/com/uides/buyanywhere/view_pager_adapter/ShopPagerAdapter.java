package com.uides.buyanywhere.view_pager_adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.ui.fragment.shop.ShopInformationFragment;
import com.uides.buyanywhere.ui.fragment.shop.ShopOrderFragment;
import com.uides.buyanywhere.ui.fragment.shop.ShopProductsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TranThanhTung on 25/11/2017.
 */

public class ShopPagerAdapter extends FragmentPagerAdapter {
    public static final int SHOP_INFORMATION_FRAGMENT_INDEX = 0;
    public static final int SHOP_PRODUCT_FRAGMENT_INDEX = 1;
    public static final int SHOP_ORDER_FRAGMENT_INDEX = 2;

    private List<Fragment> fragments;

    public ShopPagerAdapter(FragmentManager fm, Bundle shopBundle) {
        super(fm);
        fragments = new ArrayList<>(2);

        ShopInformationFragment shopInformation = new ShopInformationFragment();
        shopInformation.setArguments(shopBundle);
        fragments.add(shopInformation);
        ShopProductsFragment shopProductsFragment = new ShopProductsFragment();
        shopProductsFragment.setArguments(shopBundle);
        fragments.add(shopProductsFragment);
        boolean isShopOwner = shopBundle.getBoolean(Constant.IS_VIEW_BY_SHOP_OWNER, false);
        if(isShopOwner) {
            ShopOrderFragment shopOrderFragment = new ShopOrderFragment();
            shopOrderFragment.setArguments(shopBundle);
            fragments.add(shopOrderFragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case SHOP_INFORMATION_FRAGMENT_INDEX: {
                return "Thông tin shop";
            }

            case SHOP_PRODUCT_FRAGMENT_INDEX: {
                return "Sản phẩm shop";
            }

            case SHOP_ORDER_FRAGMENT_INDEX: {
                return "Đặt hàng";
            }

            default: {
                return "";
            }
        }
    }
}
