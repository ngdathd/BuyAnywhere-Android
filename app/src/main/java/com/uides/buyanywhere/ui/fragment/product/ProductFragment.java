package com.uides.buyanywhere.ui.fragment.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.custom_view.PriceTextView;
import com.uides.buyanywhere.custom_view.StrikeThroughPriceTextView;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Product;
import com.uides.buyanywhere.model.ProductReview;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.product.GetProductService;
import com.uides.buyanywhere.service.product.GetProductsService;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.user.CheckUserCartService;
import com.uides.buyanywhere.ui.activity.ProductDetailActivity;
import com.uides.buyanywhere.ui.fragment.RecyclerViewFragment;
import com.uides.buyanywhere.utils.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class ProductFragment extends RecyclerViewFragment implements EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener, RecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "ProductFragment";
    public static final int LIMIT_PRODUCT = 10;

    private GetProductsService getProductReviewsService;
    private GetProductService getProductService;
    private CheckUserCartService checkUserCartService;

    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initServices();
        this.loadingDialog = new LoadingDialog(getActivity());

    }

    private void initServices() {
        Network network = Network.getInstance();
        getProductReviewsService = network.createService(GetProductsService.class);
        getProductService = network.createService(GetProductService.class);
        checkUserCartService = network.createService(CheckUserCartService.class);
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
        Disposable disposable = getProductReviewsService
                .getProducts(1, LIMIT_PRODUCT, ProductReview.CREATED_DATE, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRefreshSuccess, this::onRefreshError);
        addDisposable(disposable);
    }

    private void onRefreshError(Throwable e) {
        Log.i(TAG, "onRefreshError: " + e);
        Toast.makeText(ProductFragment.this.getActivity(),
                R.string.unexpected_error_message,
                Toast.LENGTH_SHORT)
                .show();
        getSwipeRefreshLayout().setRefreshing(false);
    }

    private void onRefreshSuccess(PageResult<ProductReview> pageResult) {
        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
        productAdapter.clear();
        productAdapter.disableLoadingMore(false);
        productAdapter.addModels(pageResult.getResults(), false);
        getSwipeRefreshLayout().setRefreshing(false);
        if (pageResult.getPageIndex() == pageResult.getTotalPages()) {
            productAdapter.disableLoadingMore(true);
        }

        if (productAdapter.getItemCount() == 0) {
            showEmptyImage(true);
        } else {
            showEmptyImage(false);
        }
    }

    @Override
    public void onLoadMore() {
        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
        productAdapter.showLoadingItem(true);
        Disposable disposable = getProductReviewsService
                .getProducts(productAdapter.getItemCount() / LIMIT_PRODUCT + 1,
                        LIMIT_PRODUCT, ProductReview.CREATED_DATE,
                        1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onLoadMoreSuccess, this::onLoadMoreError);
        addDisposable(disposable);
    }

    private void onLoadMoreError(Throwable e) {
        Log.i(TAG, "onLoadMoreError: " + e);
        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
        productAdapter.hideLoadingItem();
    }

    private void onLoadMoreSuccess(PageResult<ProductReview> pageResult) {
        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
        productAdapter.hideLoadingItem();
        productAdapter.addModels(pageResult.getResults(), false);
        if (pageResult.getPageIndex() == pageResult.getTotalPages()) {
            productAdapter.disableLoadingMore(true);
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, int viewType, int position) {
        loadingDialog.show();
        ProductReview productReview = getAdapter().getItem(position, ProductReview.class);
        Observable<Product> productObservable = getProductService.getProduct(productReview.getId());
        Observable<Boolean> checkUserCartObservable = checkUserCartService.containProduct(UserAuth.getAuthUser().getAccessToken(), productReview.getId());

        addDisposable(Observable.combineLatest(productObservable, checkUserCartObservable, new BiFunction<Product, Boolean, Product>() {
            @Override
            public Product apply(Product product, Boolean isAdded) throws Exception {
                product.setAddedToCart(isAdded);
                return product;
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchProductSuccess, this::onFetchProductFailed));
    }

    private void onFetchProductSuccess(Product product) {
        loadingDialog.dismiss();
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.PRODUCT, product);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void onFetchProductFailed(Throwable e) {
        loadingDialog.dismiss();
        Log.i(TAG, "onFetchProductFailed: " + e);
        Toast.makeText(getActivity(), R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
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
            ProductReview productReview = getItem(position, ProductReview.class);
            ProductViewHolder productViewHolder = (ProductViewHolder) holder;
            Picasso.with(getActivity())
                    .load(productReview.getPreviewUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(productViewHolder.imagePreview);
            productViewHolder.textName.setText(productReview.getName());
            productViewHolder.textShop.setText(productReview.getShopName());
            long currentPrice = productReview.getCurrentPrice();
            long originPrice = productReview.getOriginPrice();
            if (currentPrice < originPrice) {
                productViewHolder.textCurrentPrice.setPrice("" + currentPrice, Constant.PRICE_UNIT);
                productViewHolder.textOriginPrice.setVisibility(View.VISIBLE);
                productViewHolder.textOriginPrice.setPrice("" + originPrice, Constant.PRICE_UNIT);
            } else {
                productViewHolder.textCurrentPrice.setPrice("" + originPrice, Constant.PRICE_UNIT);
                productViewHolder.textOriginPrice.setVisibility(View.INVISIBLE);
            }
            productViewHolder.textQuantity.setText("" + productReview.getQuantity());
            productViewHolder.tagGroup.removeAll();
            Tag tag = new Tag(productReview.getCategoryName());
            tag.tagTextSize = 11;
            productViewHolder.tagGroup.addTag(tag);
            productViewHolder.ratingBar.setStar(productReview.getRating());
            productViewHolder.textTime.setText(DateUtil.getDateDiffNow(productReview.getCreatedDate()));
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
