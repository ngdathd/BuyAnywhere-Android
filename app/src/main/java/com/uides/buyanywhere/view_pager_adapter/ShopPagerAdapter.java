package com.uides.buyanywhere.view_pager_adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uides.buyanywhere.model.Shop;
import com.uides.buyanywhere.ui.fragment.shop.FragmentShopInformation;
import com.uides.buyanywhere.ui.fragment.shop.FragmentShopProducts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TranThanhTung on 25/11/2017.
 */

public class ShopPagerAdapter extends FragmentPagerAdapter {
    public static final int SHOP_INFORMATION_FRAGMENT_INDEX = 0;
    public static final int SHOP_PRODUCT_FRAGMENT_INDEX = 1;

    private List<Fragment> fragments;

    public ShopPagerAdapter(FragmentManager fm, Bundle shopBundle) {
        super(fm);
        fragments = new ArrayList<>(2);
        FragmentShopInformation shopInformation = new FragmentShopInformation();
        shopInformation.setArguments(shopBundle);
        fragments.add(shopInformation);
        fragments.add(new FragmentShopProducts());
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

            default: {
                return "";
            }
        }
    }
}
