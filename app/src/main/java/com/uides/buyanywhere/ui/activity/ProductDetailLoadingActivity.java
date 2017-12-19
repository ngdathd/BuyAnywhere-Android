package com.uides.buyanywhere.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.model.Feedback;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Product;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.product.GetProductService;
import com.uides.buyanywhere.service.rating.GetFeedbackService;
import com.uides.buyanywhere.service.user.CheckUserCartService;
import com.uides.buyanywhere.ui.fragment.product.ProductDetailFragment;
import com.uides.buyanywhere.utils.SharedPreferencesOpenHelper;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

import static com.uides.buyanywhere.ui.fragment.product.AllProductsFragment.LATEST_FEEDBACK_COUNT;

/**
 * Created by TranThanhTung on 19/12/2017.
 */

public class ProductDetailLoadingActivity extends LoadingActivity {
    private static final String TAG = "PDActivity";

    private CompositeDisposable compositeDisposable;
    private GetProductService getProductService;
    private CheckUserCartService checkUserCartService;
    private GetFeedbackService getFeedbackService;

    private String productID;
    private boolean isAddedToCart;
    private ProductDetailFragment productDetailFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UserAuth.getAuthUser() == null) {
            UserAuth.setAuthUser(SharedPreferencesOpenHelper.getUser(this));
        }

        Intent intent = getIntent();
        productID = intent.getStringExtra(Constant.PRODUCT_ID);
        productDetailFragment = new ProductDetailFragment();

        initServices();
        showLoadingFragment();
        fetchProduct();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_blank_without_tool_bar;
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        Network network = Network.getInstance();
        getProductService = network.createService(GetProductService.class);
        checkUserCartService = network.createService(CheckUserCartService.class);
        getFeedbackService = network.createService(GetFeedbackService.class);
    }

    private void fetchProduct() {
        Observable<Product> productObservable = getProductService.getProduct(productID);
        Observable<Boolean> checkUserCartObservable = checkUserCartService.containProduct(UserAuth.getAuthUser().getAccessToken(), productID);
        Observable<PageResult<Feedback>> productFeedback = getFeedbackService.getAllFeedback(productID,
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
        showProductDetailFragment(product);
    }

    public void showProductDetailFragment(Product product) {
        Bundle bundle = new Bundle();
        boolean userIsShopOwner = product.getShopID().equals(SharedPreferencesOpenHelper.getShopID(this));
        bundle.putSerializable(Constant.PRODUCT, product);
        bundle.putBoolean(Constant.IS_VIEW_BY_SHOP_OWNER, userIsShopOwner);
        bundle.putBoolean(Constant.IS_FROM_SHOP, getIntent().getBooleanExtra(Constant.IS_FROM_SHOP, false));
        productDetailFragment.setArguments(bundle);
        showFragment(productDetailFragment);
    }

    private void onFetchProductFailed(Throwable e) {
        showErrorScreen();
        Log.i(TAG, "onFetchProductFailed: " + e);
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRetry() {
        super.onRetry();
        fetchProduct();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    public void setAddedToCart(boolean addedToCart) {
        isAddedToCart = addedToCart;
    }

    @Override
    public void onBackPressed() {
        if(!isAddedToCart) {
            Intent intent = new Intent();
            intent.putExtra(Constant.CART_REMOVED, true);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}
