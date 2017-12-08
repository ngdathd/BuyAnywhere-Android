package com.uides.buyanywhere.ui.fragment.shop;

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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.Order;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Shop;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.shop.GetShopOrderService;
import com.uides.buyanywhere.ui.fragment.RecyclerViewFragment;
import com.uides.buyanywhere.utils.DateUtil;

import java.util.ArrayList;
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
        List<Order> orders = new ArrayList<>();

        Order order = new Order();
        order.setName("Trần Thanh Tùng");
        order.setAvatarUrl("https://vignette.wikia.nocookie.net/sengokujidai/images/e/ef/Oda_Mon.png/revision/latest?cb=20111127212220");
        order.setAddress("Hoài Đức - Hà Nội");
        order.setPhone("0961569816");
        order.setProduct("Đế Lót LEGO Classic 10700 - Xanh Lá");
        order.setQuantity(5);
        order.setTime(new Date(1512745121));
        orders.add(order);

        Order order1 = new Order();
        order1.setName("Trần Thanh Tùng");
        order1.setAvatarUrl("https://vignette.wikia.nocookie.net/sengokujidai/images/e/ef/Oda_Mon.png/revision/latest?cb=20111127212220");
        order1.setAddress("Hoài Đức - Hà Nội");
        order1.setPhone("0961569816");
        order1.setProduct("Boxset Harry Potter - Tiếng Việt (Trọn Bộ 7 Tập)");
        order1.setQuantity(2);
        order1.setTime(new Date(1512710121));
        orders.add(order1);

        Order order2 = new Order();
        order2.setName("Trần Thanh Tùng");
        order2.setAvatarUrl("https://vignette.wikia.nocookie.net/sengokujidai/images/e/ef/Oda_Mon.png/revision/latest?cb=20111127212220");
        order2.setAddress("Hoài Đức - Hà Nội");
        order2.setPhone("0961569816");
        order2.setProduct("Tâm Sáng Dung Mạo Sáng");
        order2.setQuantity(10);
        order2.setTime(new Date(1512545021));
        orders.add(order2);

        Order order3 = new Order();
        order3.setName("Trần Thanh Tùng");
        order3.setAvatarUrl("https://vignette.wikia.nocookie.net/sengokujidai/images/e/ef/Oda_Mon.png/revision/latest?cb=20111127212220");
        order3.setAddress("Hoài Đức - Hà Nội");
        order3.setPhone("0961569816");
        order3.setProduct("Tã Dán Goo.n Slim Gói Đại M38 (38 Miếng)");
        order3.setQuantity(1);
        order3.setTime(new Date(1512740121));
        orders.add(order3);

        PageResult<Order> pageResult = new PageResult<>();
        pageResult.setResults(orders);
        pageResult.setPageIndex(0);
        pageResult.setPageSize(10);
        pageResult.setTotalPages(1);

        onRefreshOrderSuccess(pageResult);

//        addDisposable(getShopOrderService.getShopOrders(shopID,
//                0,
//                null,
//                Order.TIME,
//                Constant.DESC)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(this::onRefreshOrderSuccess, this::onRefreshOrderFailure));
//        ((ShopOrderAdapter) getAdapter()).disableLoadingMore(true);
    }

    private void onRefreshOrderSuccess(PageResult<Order> orderPageResult) {
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
                        Order.TIME,
                        Constant.DESC)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onLoadMoreSuccess, this::onLoadMoreError);
        addDisposable(disposable);
        getSwipeRefreshLayout().setEnabled(false);
    }

    private void onLoadMoreSuccess(PageResult<Order> pageResult) {
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

    }

    private class ShopOrderAdapter extends EndlessLoadingRecyclerViewAdapter {

        public ShopOrderAdapter(Context context) {
            super(context, false);
        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View view = getInflater().inflate(R.layout.item_order, parent, false);
            return new ShopOrderViewHolder(view);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            ShopOrderViewHolder shopOrderViewHolder = (ShopOrderViewHolder) holder;
            Order order = getItem(position, Order.class);
            Picasso.with(getActivity())
                    .load(order.getAvatarUrl())
                    .into(shopOrderViewHolder.imageAvatar);
            shopOrderViewHolder.textName.setText(order.getName());
            shopOrderViewHolder.textAddress.setText(order.getAddress());
            shopOrderViewHolder.textPhone.setText(order.getPhone());
            shopOrderViewHolder.textProduct.setText(order.getProduct());
            shopOrderViewHolder.textQuantity.setText(""+order.getQuantity());
            shopOrderViewHolder.textTime.setText(DateUtil.getDateDiffNow(order.getTime()));
            shopOrderViewHolder.buttonShipped.setVisibility(order.isShipped() ? View.INVISIBLE : View.VISIBLE);
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

        public ShopOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            buttonShipped.setOnClickListener(this);
            buttonDial.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_shipped: {

                }
                break;

                case R.id.btn_dial: {
                    int position = getAdapterPosition();
                    Order order = getAdapter().getItem(position, Order.class);
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + order.getPhone()));
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
