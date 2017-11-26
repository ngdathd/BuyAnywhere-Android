package com.uides.buyanywhere.ui.fragment.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uides.buyanywhere.R;

/**
 * Created by TranThanhTung on 25/11/2017.
 */

public class ShopUnregisteredFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop_unregistered, container, false);
        rootView.findViewById(R.id.btn_register_shop).setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        ShopFragment shopFragment = (ShopFragment) getParentFragment();
        shopFragment.startRegisterActivity();
    }
}
