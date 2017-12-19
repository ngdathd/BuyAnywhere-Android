package com.uides.buyanywhere.ui.fragment.shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.ShopOrder;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.UserProfile;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.shop.GetShopOrderService;
import com.uides.buyanywhere.service.shop.ShippedService;
import com.uides.buyanywhere.ui.activity.GuestProfileActivity;
import com.uides.buyanywhere.ui.fragment.RecyclerViewFragment;
import com.uides.buyanywhere.utils.DateUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 08/12/2017.
 */

public class ShopOrderFragment extends RecyclerViewFragment implements RecyclerViewAdapter.OnItemClickListener, EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
    private static final String TAG = "ShopOrderFragment";
    public static final int LIMIT_ORDER = 10;

    private GetShopOrderService getShopOrderService;
    private ShippedService shippedService;
    private String shopID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        shopID = bundle.getString(Constant.SHOP_ID);
        initServices();
    }

    private void initServices() {
        Network network = Network.getInstance();
        getShopOrderService = network.createService(GetShopOrderService.class);
        shippedService = network.createService(ShippedService.class);
    }

    @Override
    protected RecyclerViewAdapter initAdapter() {
        ShopOrderAdapter shopOrderAdapter = new ShopOrderAdapter(getActivity());
        shopOrderAdapter.setOnItemClickListener(this);
        shopOrderAdapter.setLoadingMoreListener(this);
        return shopOrderAdapter;
    }

    @Override
    public void onRefresh() {
        addDisposable(getShopOrderService.getShopOrders(shopID,
                0,
                null,
                ShopOrder.TIME,
                Constant.DESC)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRefreshOrderSuccess, this::onRefreshOrderFailure));
        ((ShopOrderAdapter) getAdapter()).disableLoadingMore(true);
    }

    private void onRefreshOrderSuccess(PageResult<ShopOrder> orderPageResult) {
        ShopOrderAdapter shopOrderAdapter = (ShopOrderAdapter) getAdapter();
        shopOrderAdapter.disableLoadingMore(false);
        shopOrderAdapter.clear();
        shopOrderAdapter.disableLoadingMore(false);
        shopOrderAdapter.addModels(orderPageResult.getResults(), false);
        getSwipeRefreshLayout().setRefreshing(false);
        if (orderPageResult.getPageIndex() == orderPageResult.getTotalPages()) {
            shopOrderAdapter.disableLoadingMore(true);
        }
    }

    private void onRefreshOrderFailure(Throwable e) {
        ((EndlessLoadingRecyclerViewAdapter) getAdapter()).disableLoadingMore(false);
        Log.i(TAG, "onRefreshError: " + e);
        Toast.makeText(getActivity(),
                R.string.unexpected_error_message,
                Toast.LENGTH_SHORT)
                .show();
        getSwipeRefreshLayout().setRefreshing(false);
    }

    @Override
    public void onLoadMore() {
        ShopOrderAdapter shopOrderAdapter = (ShopOrderAdapter) getAdapter();
        shopOrderAdapter.showLoadingItem(true);

        Disposable disposable = getShopOrderService
                .getShopOrders(shopID,
                        shopOrderAdapter.getItemCount() / LIMIT_ORDER + 1,
                        LIMIT_ORDER,
                        ShopOrder.TIME,
                        Constant.DESC)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onLoadMoreSuccess, this::onLoadMoreError);
        addDisposable(disposable);
        getSwipeRefreshLayout().setEnabled(false);
    }

    private void onLoadMoreSuccess(PageResult<ShopOrder> pageResult) {
        getSwipeRefreshLayout().setEnabled(true);
        ShopOrderAdapter shopOrderAdapter = (ShopOrderAdapter) getAdapter();
        shopOrderAdapter.hideLoadingItem();
        shopOrderAdapter.addModels(pageResult.getResults(), false);
        if (pageResult.getPageIndex() == pageResult.getTotalPages()) {
            shopOrderAdapter.disableLoadingMore(true);
        }
    }

    private void onLoadMoreError(Throwable e) {
        getSwipeRefreshLayout().setEnabled(true);
        Log.i(TAG, "onLoadMoreError: " + e);
        ShopOrderAdapter shopOrderAdapter = (ShopOrderAdapter) getAdapter();
        shopOrderAdapter.hideLoadingItem();
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, int viewType, int position) {
        ShopOrder shopOrder = getAdapter().getItem(position, ShopOrder.class);
        Intent intent = new Intent(getActivity(), GuestProfileActivity.class);
        intent.putExtra(Constant.USER_ID, shopOrder.getUserID());
        startActivity(intent);
    }

    private class ShopOrderAdapter extends EndlessLoadingRecyclerViewAdapter {

        public ShopOrderAdapter(Context context) {
            super(context, false);
        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View view = getInflater().inflate(R.layout.item_shop_order, parent, false);
            return new ShopOrderViewHolder(view);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            ShopOrderViewHolder shopOrderViewHolder = (ShopOrderViewHolder) holder;
            ShopOrder shopOrder = getItem(position, ShopOrder.class);
            Activity activity = getActivity();

            Picasso.with(activity)
                    .load(shopOrder.getAvatarUrl())
                    .into(shopOrderViewHolder.imageAvatar);
            shopOrderViewHolder.textName.setText(shopOrder.getName());
            shopOrderViewHolder.textAddress.setText(shopOrder.getAddress());
            shopOrderViewHolder.textPhone.setText(shopOrder.getPhone());
            shopOrderViewHolder.textProduct.setText(shopOrder.getProductName());
            shopOrderViewHolder.textQuantity.setText("" + shopOrder.getQuantity());
            shopOrderViewHolder.textTime.setText(DateUtil.getDateDiffNow(shopOrder.getOrderDate()));
            Date shippedDate = shopOrder.getShippedDate();
            Button buttonShipped = shopOrderViewHolder.buttonShipped;
            if (shippedDate == null) {
                buttonShipped.setEnabled(true);
                buttonShipped.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                buttonShipped.setText(R.string.ship_complete);
            } else {
                buttonShipped.setEnabled(false);
                buttonShipped.setTextColor(activity.getResources().getColor(R.color.dark_gray));
                buttonShipped.setText(R.string.shipped);
            }

            shopOrderViewHolder.showProgress(shopOrder, shopOrder.isShipping());
        }

        @Override
        protected RecyclerView.ViewHolder initLoadingViewHolder(ViewGroup parent) {
            View view = getInflater().inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

        @Override
        protected void bindLoadingViewHolder(LoadingViewHolder loadingViewHolder, int position) {

        }

        @Override
        public <T> void addModels(List<T> listModels, boolean isScroll) {
            super.addModels(listModels, isScroll);
            if (getItemCount() == 0) {
                showEmptyImage(true);
            } else {
                showEmptyImage(false);
            }
        }

        @Override
        public void addModel(Object model, boolean isScroll) {
            super.addModel(model, isScroll);
            if (getItemCount() == 0) {
                showEmptyImage(true);
            } else {
                showEmptyImage(false);
            }
        }
    }

    public class ShopOrderViewHolder extends RecyclerViewAdapter.NormalViewHolder implements View.OnClickListener {
        @BindView(R.id.img_avatar)
        ImageView imageAvatar;
        @BindView(R.id.txt_name)
        TextView textName;
        @BindView(R.id.txt_address)
        TextView textAddress;
        @BindView(R.id.txt_phone)
        TextView textPhone;
        @BindView(R.id.txt_product)
        TextView textProduct;
        @BindView(R.id.txt_quantity)
        TextView textQuantity;
        @BindView(R.id.txt_time)
        TextView textTime;
        @BindView(R.id.btn_shipped)
        Button buttonShipped;
        @BindView(R.id.btn_dial)
        Button buttonDial;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        public ShopOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            buttonShipped.setOnClickListener(this);
            buttonDial.setOnClickListener(this);
        }

        public void showProgress(ShopOrder shopOrder, boolean isShow) {
            shopOrder.setShipping(isShow);
            progressBar.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
            buttonShipped.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_shipped: {
                    int position = getAdapterPosition();
                    ShopOrder shopOrder = getAdapter().getItem(position, ShopOrder.class);

                    showProgress(shopOrder, true);

                    addDisposable(shippedService.ship(shopOrder.getId())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(success -> {
                                showProgress(shopOrder, false);
                                shopOrder.setShippedDate(success.getShippedDate());

                                buttonShipped.setEnabled(false);
                                buttonShipped.setTextColor(getActivity().getResources().getColor(R.color.dark_gray));
                                buttonShipped.setText(R.string.shipped);
                            }, failure -> {
                                showProgress(shopOrder, false);
                                Log.i(TAG, "onShipFailure: " + failure);
                                Toast.makeText(getActivity(), R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
                            }));
                }
                break;

                case R.id.btn_dial: {
                    int position = getAdapterPosition();
                    ShopOrder shopOrder = getAdapter().getItem(position, ShopOrder.class);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + shopOrder.getPhone()));
                    startActivity(intent);
                }
                break;

                default: {
                    break;
                }
            }
        }
    }
}
