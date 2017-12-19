package com.uides.buyanywhere.ui.fragment.shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.hedgehog.ratingbar.RatingBar;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.PriceTextView;
import com.uides.buyanywhere.custom_view.StrikeThroughPriceTextView;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Product;
import com.uides.buyanywhere.model.ProductPreview;
import com.uides.buyanywhere.model.User;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.product.GetProductService;
import com.uides.buyanywhere.service.shop.GetShopProductsService;
import com.uides.buyanywhere.ui.activity.PostProductActivity;
import com.uides.buyanywhere.ui.activity.ProductDetailActivity;
import com.uides.buyanywhere.ui.activity.ProductDetailLoadingActivity;
import com.uides.buyanywhere.ui.fragment.RecyclerViewFragment;
import com.uides.buyanywhere.utils.DateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 25/11/2017.
 */

public class ShopProductsFragment extends RecyclerViewFragment implements EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener,
        RecyclerViewAdapter.OnItemClickListener, View.OnClickListener {
    private static final int POST_PRODUCT_REQUEST_CODE = 0;
    private static final String TAG = "ShopProductsFragment";
    public static final int LIMIT_PRODUCT = 10;

    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    private GetShopProductsService getShopProductsService;
    private String shopID;
    private boolean isGuest;

    @Override
    protected int getLayout() {
        return R.layout.fragment_shop_product;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        shopID = bundle.getString(Constant.SHOP_ID);
        isGuest = bundle.getBoolean(Constant.IS_GUEST, false);
        initServices();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        if(isGuest) {
            fabAdd.setVisibility(View.INVISIBLE);
        } else {
            fabAdd.setOnClickListener(this);
        }
        return rootView;
    }

    private void initServices() {
        Network network = Network.getInstance();
        getShopProductsService = network.createService(GetShopProductsService.class);
    }

    @Override
    protected RecyclerViewAdapter initAdapter() {
        ProductAdapter productAdapter = new ProductAdapter(getActivity());
        productAdapter.setLoadingMoreListener(this);
        productAdapter.setOnItemClickListener(this);
        return productAdapter;
    }

    @Override
    public void onRefresh() {
        User user = UserAuth.getAuthUser();
        Disposable disposable = getShopProductsService
                .getShopProducts(user.getAccessToken(),
                        shopID,
                        1,
                        LIMIT_PRODUCT,
                        ProductPreview.CREATED_DATE,
                        1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRefreshSuccess, this::onRefreshError);
        addDisposable(disposable);
        ((EndlessLoadingRecyclerViewAdapter) getAdapter()).disableLoadingMore(true);
    }

    private void onRefreshError(Throwable e) {
        ((EndlessLoadingRecyclerViewAdapter) getAdapter()).disableLoadingMore(false);
        Log.i(TAG, "onRefreshError: " + e);
        Toast.makeText(getActivity(),
                R.string.unexpected_error_message,
                Toast.LENGTH_SHORT)
                .show();
        getSwipeRefreshLayout().setRefreshing(false);
    }

    private void onRefreshSuccess(PageResult<ProductPreview> pageResult) {
        ((EndlessLoadingRecyclerViewAdapter) getAdapter()).disableLoadingMore(false);
        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
        productAdapter.clear();
        productAdapter.disableLoadingMore(false);
        productAdapter.addModels(pageResult.getResults(), false);
        getSwipeRefreshLayout().setRefreshing(false);
        if (pageResult.getPageIndex() == pageResult.getTotalPages()) {
            productAdapter.disableLoadingMore(true);
        }
    }

    @Override
    public void onLoadMore() {
        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
        productAdapter.showLoadingItem(true);
        User user = UserAuth.getAuthUser();
        Disposable disposable = getShopProductsService
                .getShopProducts(user.getAccessToken(),
                        user.getShopID(),
                        productAdapter.getItemCount() / LIMIT_PRODUCT + 1,
                        LIMIT_PRODUCT, ProductPreview.CREATED_DATE,
                        1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onLoadMoreSuccess, this::onLoadMoreError);
        addDisposable(disposable);
        getSwipeRefreshLayout().setEnabled(false);
    }

    private void onLoadMoreError(Throwable e) {
        getSwipeRefreshLayout().setEnabled(true);
        Log.i(TAG, "onLoadMoreError: " + e);
        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
        productAdapter.hideLoadingItem();
    }

    private void onLoadMoreSuccess(PageResult<ProductPreview> pageResult) {
        getSwipeRefreshLayout().setEnabled(true);
        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
        productAdapter.hideLoadingItem();
        productAdapter.addModels(pageResult.getResults(), false);
        if (pageResult.getPageIndex() == pageResult.getTotalPages()) {
            productAdapter.disableLoadingMore(true);
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, int viewType, int position) {
        ProductPreview productPreview = getAdapter().getItem(position, ProductPreview.class);
        Intent intent = new Intent(getActivity(), ProductDetailLoadingActivity.class);
        intent.putExtra(Constant.PRODUCT_ID, productPreview.getId());
        intent.putExtra(Constant.IS_FROM_SHOP, true);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add: {
                Intent intent = new Intent(getActivity(), PostProductActivity.class);
                startActivityForResult(intent, POST_PRODUCT_REQUEST_CODE);
            }
            break;

            default: {
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case POST_PRODUCT_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    ProductPreview productPreview = (ProductPreview) data.getSerializableExtra(Constant.PRODUCT_REVIEW);
                    getAdapter().addModel(0, productPreview, false);
                }
            }
            break;

            default: {
                break;
            }
        }
    }

    private class ProductAdapter extends EndlessLoadingRecyclerViewAdapter {
        ProductAdapter(Context context) {
            super(context, false);
        }

        @Override
        protected RecyclerView.ViewHolder initLoadingViewHolder(ViewGroup parent) {
            View loadingView = getInflater().inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(loadingView);
        }

        @Override
        protected void bindLoadingViewHolder(LoadingViewHolder loadingViewHolder, int position) {

        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View productView = getInflater().inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(productView);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            ProductPreview productPreview = getItem(position, ProductPreview.class);
            ProductViewHolder productViewHolder = (ProductViewHolder) holder;
            Picasso.with(getActivity())
                    .load(productPreview.getPreviewUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .into(productViewHolder.imagePreview);
            productViewHolder.textName.setText(productPreview.getName());
            productViewHolder.textShop.setText(productPreview.getShopName());
            long currentPrice = productPreview.getCurrentPrice();
            long originPrice = productPreview.getOriginPrice();
            if (currentPrice < originPrice) {
                productViewHolder.textCurrentPrice.setPrice("" + currentPrice, Constant.PRICE_UNIT);
                productViewHolder.textOriginPrice.setVisibility(View.VISIBLE);
                productViewHolder.textOriginPrice.setPrice("" + originPrice, Constant.PRICE_UNIT);
            } else {
                productViewHolder.textCurrentPrice.setPrice("" + originPrice, Constant.PRICE_UNIT);
                productViewHolder.textOriginPrice.setVisibility(View.INVISIBLE);
            }
            productViewHolder.textQuantity.setText("" + productPreview.getQuantity());
            productViewHolder.tagGroup.removeAll();
            Tag tag = new Tag(productPreview.getCategoryName());
            tag.tagTextSize = 11;
            productViewHolder.tagGroup.addTag(tag);
            productViewHolder.ratingBar.setStar(productPreview.getRating());
            productViewHolder.textTime.setText(DateUtil.getDateDiffNow(productPreview.getCreatedDate()));
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

    class ProductViewHolder extends RecyclerViewAdapter.NormalViewHolder {
        @BindView(R.id.img_preview)
        ImageView imagePreview;
        @BindView(R.id.txt_name)
        TextView textName;
        @BindView(R.id.txt_shop_name)
        TextView textShop;
        @BindView(R.id.txt_current_price)
        PriceTextView textCurrentPrice;
        @BindView(R.id.txt_origin_price)
        StrikeThroughPriceTextView textOriginPrice;
        @BindView(R.id.txt_quantity)
        TextView textQuantity;
        @BindView(R.id.tag_group)
        TagView tagGroup;
        @BindView(R.id.rating_bar)
        RatingBar ratingBar;
        @BindView(R.id.txt_time)
        TextView textTime;

        ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
