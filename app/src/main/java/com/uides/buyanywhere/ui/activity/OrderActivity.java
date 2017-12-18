package com.uides.buyanywhere.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.model.Feedback;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Product;
import com.uides.buyanywhere.model.UserOrder;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.product.GetProductService;
import com.uides.buyanywhere.service.rating.GetFeedbackService;
import com.uides.buyanywhere.service.user.CheckUserCartService;
import com.uides.buyanywhere.service.user.GetUserOrderService;
import com.uides.buyanywhere.utils.DateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

import static com.uides.buyanywhere.ui.fragment.product.AllProductsFragment.LATEST_FEEDBACK_COUNT;

/**
 * Created by TranThanhTung on 18/12/2017.
 */

public class OrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener, RecyclerViewAdapter.OnItemClickListener {
    private static final int LIMIT_ORDER = 10;
    private static final String TAG = "OrderActivity";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.img_empty)
    ImageView imageEmpty;

    UserOrderAdapter userOrderAdapter;

    private CompositeDisposable compositeDisposable;
    private GetUserOrderService getUserOrderService;
    private GetProductService getProductService;
    private CheckUserCartService checkUserCartService;
    private GetFeedbackService getFeedbackService;

    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        userOrderAdapter = new UserOrderAdapter(this);
        userOrderAdapter.setLoadingMoreListener(this);
        userOrderAdapter.setOnItemClickListener(this);

        initServices();
        initToolBar();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userOrderAdapter.getItemCount() == 0) {
            showEmptyImage(true);
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.shopOrder);
        }
    }

    private void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userOrderAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        int firstColor = getResources().getColor(R.color.blue);
        int secondColor = getResources().getColor(R.color.red);
        int thirdColor = getResources().getColor(R.color.yellow);
        int fourthColor = getResources().getColor(R.color.green);
        swipeRefreshLayout.setColorSchemeColors(firstColor, secondColor, thirdColor, fourthColor);
        swipeRefreshLayout.setOnRefreshListener(this);

        this.loadingDialog = new LoadingDialog(this);
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        Network network = Network.getInstance();
        getUserOrderService = network.createService(GetUserOrderService.class);
        getProductService = network.createService(GetProductService.class);
        checkUserCartService = network.createService(CheckUserCartService.class);
        getFeedbackService = network.createService(GetFeedbackService.class);
    }

    @Override
    public void onLoadMore() {
        userOrderAdapter.showLoadingItem(true);

        compositeDisposable.add(getUserOrderService.getUserOrder(UserAuth.getAuthUser().getId(),
                        userOrderAdapter.getItemCount() / LIMIT_ORDER + 1,
                        LIMIT_ORDER,
                        UserOrder.ORDER_DATE,
                        Constant.DESC)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onLoadMoreSuccess, this::onLoadMoreError));
        swipeRefreshLayout.setEnabled(false);
    }

    private void onLoadMoreSuccess(PageResult<UserOrder> pageResult) {
        swipeRefreshLayout.setEnabled(true);
        userOrderAdapter.hideLoadingItem();
        userOrderAdapter.addModels(pageResult.getResults(), false);
        if (pageResult.getPageIndex() == pageResult.getTotalPages()) {
            userOrderAdapter.disableLoadingMore(true);
        }
    }

    private void onLoadMoreError(Throwable e) {
        swipeRefreshLayout.setEnabled(true);
        Log.i(TAG, "onLoadMoreError: " + e);
        userOrderAdapter.hideLoadingItem();
    }

    @Override
    public void onRefresh() {
        userOrderAdapter.disableLoadingMore(true);
        compositeDisposable.add(getUserOrderService.getUserOrder(UserAuth.getAuthUser().getId(),
                0,
                LIMIT_ORDER,
                UserOrder.ORDER_DATE,
                Constant.DESC)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRefreshUserOrderSuccess, this::onRefreshUserOrderFailed));
    }

    private void onRefreshUserOrderSuccess(PageResult<UserOrder> userOrderPageResult) {
        swipeRefreshLayout.setRefreshing(false);
        userOrderAdapter.disableLoadingMore(false);
        userOrderAdapter.clear();
        userOrderAdapter.addModels(userOrderPageResult.getResults(), false);

        if (userOrderPageResult.getPageIndex() == userOrderPageResult.getTotalPages()) {
            userOrderAdapter.disableLoadingMore(true);
        }

        showEmptyImage(userOrderAdapter.getItemCount() == 0);
    }

    private void showEmptyImage(boolean isShow) {
        imageEmpty.setVisibility(isShow? View.VISIBLE : View.INVISIBLE);
    }

    private void onRefreshUserOrderFailed(Throwable e) {
        Log.i(TAG, "onRefreshUserOrderFailed: " + e);
        swipeRefreshLayout.setRefreshing(false);
        userOrderAdapter.disableLoadingMore(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, int viewType, int position) {
        loadingDialog.show();
        UserOrder userOrder = userOrderAdapter.getItem(position, UserOrder.class);
        Observable<Product> productObservable = getProductService.getProduct(userOrder.getProductName());
        Observable<Boolean> checkUserCartObservable = checkUserCartService.containProduct(UserAuth.getAuthUser().getAccessToken(),
                userOrder.getId());
        Observable<PageResult<Feedback>> productFeedback = getFeedbackService.getAllFeedback(userOrder.getId(),
                0,
                LATEST_FEEDBACK_COUNT,
                Feedback.CREATED_DATE,
                Constant.DESC);

        compositeDisposable.add(Observable.combineLatest(productObservable, checkUserCartObservable, productFeedback, new Function3<Product, Boolean, PageResult<Feedback>, Product>() {
            @Override
            public Product apply(Product product, Boolean isAdded, PageResult<Feedback> feedbackPageResult) throws Exception {
                product.setAddedToCart(isAdded);
                product.setPreviewFeedback(feedbackPageResult.getResults());
                return product;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchProductSuccess, this::onFetchProductFailed));
    }

    private void onFetchProductSuccess(Product product) {
        loadingDialog.dismiss();
        Intent intent = new Intent(this, ProductDetailActivity.class);
        Bundle bundle = new Bundle();

        boolean userIsShopOwner = UserAuth.getAuthUser().getShopID().equals(product.getShopID());
        bundle.putSerializable(Constant.PRODUCT, product);
        bundle.putBoolean(Constant.IS_VIEW_BY_SHOP_OWNER, userIsShopOwner);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void onFetchProductFailed(Throwable e) {
        loadingDialog.dismiss();
        Log.i(TAG, "onFetchProductFailed: " + e);
        Toast.makeText(this, R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    private class UserOrderAdapter extends EndlessLoadingRecyclerViewAdapter {

        public UserOrderAdapter(Context context) {
            super(context, false);
        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View view = getInflater().inflate(R.layout.item_user_order, parent, false);
            return new UserOrderViewHolder(view);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            UserOrderViewHolder userOrderViewHolder = (UserOrderViewHolder) holder;
            UserOrder userOrder = getItem(position, UserOrder.class);

            Picasso.with(OrderActivity.this)
                    .load(userOrder.getPreviewUrl())
                    .into(userOrderViewHolder.imagePreview);
            userOrderViewHolder.textProduct.setText(userOrder.getProductName());
            userOrderViewHolder.textAddress.setText(userOrder.getAddress());
            userOrderViewHolder.textPhone.setText(userOrder.getPhone());
            userOrderViewHolder.textShopName.setText(userOrder.getShopName());
            userOrderViewHolder.textQuantity.setText("" + userOrder.getQuantity());
            userOrderViewHolder.categoryTags.removeAll();
            Tag tag = new Tag(userOrder.getCategoryName());
            tag.tagTextSize = 11;
            userOrderViewHolder.categoryTags.addTag(tag);
            userOrderViewHolder.textTime.setText(DateUtil.getDateDiffNow(userOrder.getOrderDate()));
            userOrderViewHolder.imageShipped.setVisibility(userOrder.getShippedDate() == null? View.INVISIBLE : View.VISIBLE);
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

    public class UserOrderViewHolder extends RecyclerViewAdapter.NormalViewHolder implements View.OnClickListener {
        @BindView(R.id.img_preview)
        ImageView imagePreview;
        @BindView(R.id.txt_product)
        TextView textProduct;
        @BindView(R.id.txt_shop_name)
        TextView textShopName;
        @BindView(R.id.txt_address)
        TextView textAddress;
        @BindView(R.id.txt_phone)
        TextView textPhone;
        @BindView(R.id.tag_group)
        TagView categoryTags;
        @BindView(R.id.txt_quantity)
        TextView textQuantity;
        @BindView(R.id.txt_time)
        TextView textTime;
        @BindView(R.id.img_shipped)
        ImageView imageShipped;
        @BindView(R.id.btn_dial)
        Button buttonDial;

        public UserOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            buttonDial.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_dial: {
                    int position = getAdapterPosition();
                    UserOrder userOrder = userOrderAdapter.getItem(position, UserOrder.class);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + userOrder.getPhone()));
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
