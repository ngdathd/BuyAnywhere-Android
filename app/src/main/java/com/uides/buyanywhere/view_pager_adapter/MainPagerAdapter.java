package com.uides.buyanywhere.view_pager_adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uides.buyanywhere.R;
import com.uides.buyanywhere.ui.fragment.product.ProductFragment;
import com.uides.buyanywhere.ui.fragment.location.FindByLocationFragment;
import com.uides.buyanywhere.ui.fragment.profile.ProfileFragment;
import com.uides.buyanywhere.ui.fragment.shop.ShopFragment;
import com.uides.buyanywhere.ui.fragment.cart.ShoppingCartFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    public static final int PRODUCT_FRAGMENT_INDEX = 0;
    public static final int FIND_BY_LOCATION_FRAGMENT_INDEX = 1;
    public static final int SHOPPING_CART_FRAGMENT_INDEX = 2;
    public static final int SHOP_FRAGMENT_INDEX = 3;
    public static final int PROFILE_FRAGMENT_INDEX = 4;
    private List<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments() {
        fragments = new ArrayList<>(4);
        fragments.add(new ProductFragment());
        fragments.add(new FindByLocationFragment());
        fragments.add(new ShoppingCartFragment());
        fragments.add(new ShopFragment());
        fragments.add(new ProfileFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public static int getNavigationButtonID(int position) {
        switch (position) {
            case FIND_BY_LOCATION_FRAGMENT_INDEX: {
                return R.id.navigation_location;
            }

            case SHOPPING_CART_FRAGMENT_INDEX: {
                return R.id.navigation_shopping_cart;
            }

            case SHOP_FRAGMENT_INDEX: {
                return R.id.navigation_shop;
            }

            case PROFILE_FRAGMENT_INDEX: {
                return R.id.navigation_profile;

            }

            default: {
                return R.id.navigation_product;
            }
        }
    }
}
