package com.uides.buyanywhere.ui.fragment.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.Shop;
import com.uides.buyanywhere.view_pager_adapter.ShopPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 25/11/2017.
 */

public class ShopPagerFragment extends Fragment {
    private static final String TAG = "ShopPagerFragment";
    @BindView(R.id.view_pager_shop)
    ViewPager shopViewPager;
    @BindView(R.id.tab_layout_shop)
    TabLayout tabLayout;
    @BindView(R.id.img_avatar)
    ImageView imageAvatar;
    @BindView(R.id.img_cover)
    ImageView imageCover;

    private ShopPagerAdapter shopPagerAdapter;
    private String coverUrl;
    private String avatarUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Shop shop = (Shop) bundle.getSerializable(Constant.SHOP);
        if (shop != null) {
            coverUrl = shop.getCoverUrl();
            avatarUrl = shop.getAvatarUrl();
        }
        initAdapter(bundle);
    }

    private void initAdapter(Bundle bundle) {
        shopPagerAdapter = new ShopPagerAdapter(getChildFragmentManager(), bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop, container, false);
        ButterKnife.bind(this, rootView);
        showViews();
        return rootView;
    }

    private void showViews() {
        shopViewPager.setAdapter(shopPagerAdapter);
        showImage(avatarUrl, coverUrl);
    }

    public void onShopUpdated(Shop shop) {
        this.avatarUrl = shop.getAvatarUrl();
        this.coverUrl = shop.getCoverUrl();
        showImage(avatarUrl, coverUrl);
        ((ChildShopFragment) getParentFragment()).onShopUpdated(shop);
    }

    private void showImage(String avatarUrl, String coverUrl) {
        Picasso.with(getActivity()).load(avatarUrl)
                .placeholder(R.drawable.shop_avatar_placeholder)
                .fit()
                .into(imageAvatar);

        Picasso.with(getActivity()).load(coverUrl)
                .placeholder(R.drawable.shop_cover_place_holder)
                .fit()
                .into(imageCover);
    }
}
