package com.uides.buyanywhere.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.Shop;
import com.uides.buyanywhere.network.retrofit.Network;
import com.uides.buyanywhere.network.service.ShopService;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 08/11/2017.
 */

public class ShopRecyclerViewActivity extends AppCompatActivity {
    @BindView(R.id.shop_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refreshing)
    SwipeRefreshLayout swipeRefreshLayout;

    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_shop_recyclerview);
        ButterKnife.bind(this);

        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myRecyclerViewAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ShopService shopService = Network.getInstance().createService(ShopService.class);
                Observable<List<Shop>> observable = shopService.get();
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Observer<List<Shop>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(ShopRecyclerViewActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onNext(List<Shop> shops) {
                                myRecyclerViewAdapter.clear();
                                myRecyclerViewAdapter.addModels(shops, false);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
            }
        });
        initNavigationBar();
    }

    private void initNavigationBar() {
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Tab 1", android.R.color.holo_blue_light);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Tab 2", android.R.color.holo_green_light);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Tab 3", android.R.color.holo_orange_light);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

// Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
    }

    private class MyRecyclerViewAdapter extends EndlessLoadingRecyclerViewAdapter {
        public MyRecyclerViewAdapter(Context context, boolean enableSelectedMode) {
            super(context, enableSelectedMode);
        }

        @Override
        protected RecyclerView.ViewHolder initLoadingViewHolder(ViewGroup parent) {
            View loadingView = getLayoutInflater().inflate(R.layout.recycler_view_loading_view, parent, false);
            return new LoadingViewHolder(loadingView);
        }

        @Override
        protected void bindLoadingViewHolder(LoadingViewHolder loadingViewHolder, int position) {

        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View itemView = getInflater().inflate(R.layout.shop_item, parent, false);
            return new ItemViewHolder(itemView);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Shop shop = getItem(position, Shop.class);
            itemViewHolder.txtEmail.setText(shop.getEmail());
            itemViewHolder.txtName.setText(shop.getName());
            itemViewHolder.txtPhone.setText(shop.getPhone());
            Picasso.with(getInflater().getContext())
                    .load(shop.getWebsite())
                    .into(itemViewHolder.imgLogo);
        }
    }

    private class ItemViewHolder extends EndlessLoadingRecyclerViewAdapter.NormalViewHolder {
        private ImageView imgLogo;
        private TextView txtName;
        private TextView txtPhone;
        private TextView txtEmail;
        private TextView txtWebsite;


        public ItemViewHolder(View itemView) {
            super(itemView);

            imgLogo = (ImageView) itemView.findViewById(R.id.img_view);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtPhone = (TextView) itemView.findViewById(R.id.txt_phone);
            txtEmail = (TextView) itemView.findViewById(R.id.txt_email);
            txtWebsite = (TextView) itemView.findViewById(R.id.txt_website);

        }
    }

}
