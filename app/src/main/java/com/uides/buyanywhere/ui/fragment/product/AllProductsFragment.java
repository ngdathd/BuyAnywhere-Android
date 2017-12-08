package com.uides.buyanywhere.ui.fragment.product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.adapter.ProductAdapter;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.model.Feedback;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Product;
import com.uides.buyanywhere.model.ProductReview;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.product.GetProductService;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.product.GetProductsService;
import com.uides.buyanywhere.service.rating.GetFeedbackService;
import com.uides.buyanywhere.service.user.CheckUserCartService;
import com.uides.buyanywhere.ui.activity.ProductDetailActivity;
import com.uides.buyanywhere.ui.fragment.RecyclerViewFragment;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class AllProductsFragment extends RecyclerViewFragment implements EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener, RecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "AllProductsFragment";
    public static final int LIMIT_PRODUCT = 10;
    public static final int LATEST_FEEDBACK_COUNT = 3;
    private static final int PRODUCT_DETAIL_REQUEST_CODE = 0;

    private GetProductsService getProductReviewsService;
    private GetProductService getProductService;
    private CheckUserCartService checkUserCartService;
    private GetFeedbackService getFeedbackService;

    private LoadingDialog loadingDialog;
    private int selectedProductIndex = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initServices();
        this.loadingDialog = new LoadingDialog(getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {

            }
            break;

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initServices() {
        Network network = Network.getInstance();
        getProductReviewsService = network.createService(GetProductsService.class);
        getProductService = network.createService(GetProductService.class);
        checkUserCartService = network.createService(CheckUserCartService.class);
        getFeedbackService = network.createService(GetFeedbackService.class);
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
        ((EndlessLoadingRecyclerViewAdapter) getAdapter()).disableLoadingMore(true);
    }

    private void onRefreshError(Throwable e) {
        ((EndlessLoadingRecyclerViewAdapter) getAdapter()).disableLoadingMore(false);
        Log.i(TAG, "onRefreshError: " + e);
        Toast.makeText(AllProductsFragment.this.getActivity(),
                R.string.unexpected_error_message,
                Toast.LENGTH_SHORT)
                .show();
        getSwipeRefreshLayout().setRefreshing(false);
    }

    private void onRefreshSuccess(PageResult<ProductReview> pageResult) {
        ((EndlessLoadingRecyclerViewAdapter) getAdapter()).disableLoadingMore(false);
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
        getSwipeRefreshLayout().setEnabled(false);
    }

    private void onLoadMoreError(Throwable e) {
        getSwipeRefreshLayout().setEnabled(true);
        Log.i(TAG, "onLoadMoreError: " + e);
        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
        productAdapter.hideLoadingItem();
    }

    private void onLoadMoreSuccess(PageResult<ProductReview> pageResult) {
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
        loadingDialog.show();
        ProductReview productReview = getAdapter().getItem(position, ProductReview.class);
        Observable<Product> productObservable = getProductService.getProduct(productReview.getId());
        Observable<Boolean> checkUserCartObservable = checkUserCartService.containProduct(UserAuth.getAuthUser().getAccessToken(),
                productReview.getId());
        Observable<PageResult<Feedback>> productFeedback = getFeedbackService.getAllFeedback(productReview.getId(),
                0,
                LATEST_FEEDBACK_COUNT,
                Feedback.CREATED_DATE,
                Constant.DESC);

        addDisposable(Observable.combineLatest(productObservable, checkUserCartObservable, productFeedback, new Function3<Product, Boolean, PageResult<Feedback>, Product>() {
            @Override
            public Product apply(Product product, Boolean isAdded, PageResult<Feedback> feedbackPageResult) throws Exception {
                product.setAddedToCart(isAdded);
                product.setPreviewFeedback(feedbackPageResult.getResults());
                return product;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> onFetchProductSuccess(success, position), this::onFetchProductFailed));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PRODUCT_DETAIL_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    if (selectedProductIndex != -1) {
                        ProductAdapter productAdapter = (ProductAdapter) getAdapter();
                        ProductReview product = productAdapter.getItem(selectedProductIndex, ProductReview.class);
                        int rating = data.getIntExtra(Constant.RATING, product.getRating());
                        product.setRating(rating);
                        productAdapter.notifyItemChanged(selectedProductIndex);
                        selectedProductIndex = -1;
                    }
                }
            }
            break;

            default: {
                break;
            }
        }
    }

    private void onFetchProductSuccess(Product product, int index) {
        loadingDialog.dismiss();
        this.selectedProductIndex = index;
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        Bundle bundle = new Bundle();

        boolean userIsShopOwner = UserAuth.getAuthUser().getShopID().equals(product.getShopID());
        bundle.putSerializable(Constant.PRODUCT, product);
        bundle.putBoolean(Constant.IS_VIEW_BY_SHOP_OWNER, userIsShopOwner);
        intent.putExtras(bundle);
        startActivityForResult(intent, PRODUCT_DETAIL_REQUEST_CODE);
    }

    private void onFetchProductFailed(Throwable e) {
        loadingDialog.dismiss();
        this.selectedProductIndex = -1;
        Log.i(TAG, "onFetchProductFailed: " + e);
        Toast.makeText(getActivity(), R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }
}
