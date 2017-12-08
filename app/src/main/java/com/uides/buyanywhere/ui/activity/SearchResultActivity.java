package com.uides.buyanywhere.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.adapter.ProductAdapter;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Product;
import com.uides.buyanywhere.model.ProductReview;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.categories.GetCategoriesService;
import com.uides.buyanywhere.service.product.FilterProductsService;
import com.uides.buyanywhere.service.product.GetProductService;
import com.uides.buyanywhere.service.user.CheckUserCartService;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 27/11/2017.
 */

public class SearchResultActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener, EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener, RecyclerViewAdapter.OnItemClickListener {
    public static final String TAG = "SearchResultActivity";

    private FilterProductsService filterProductsService;
    private GetProductService getProductService;
    private GetCategoriesService getCategoriesService;
    private CheckUserCartService checkUserCartService;
    private CompositeDisposable compositeDisposable;

    private List<Category> categories;

    private SearchView searchView;
    private MenuItem filterItem;
    private MenuItem sortItem;
    private PopupMenu popupFilterMenu;
    private PopupMenu popupSortMenu;

    @BindView(R.id.ln_error)
    LinearLayout errorLayout;
    @BindView(R.id.av_loading_indicator)
    AVLoadingIndicatorView avLoadingIndicatorView;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.img_empty)
    ImageView imageEmpty;

    private String name;
    private String category;
    private String orderBy = Product.CREATED_DATE;
    private String orderType = Constant.DESC;

    private ProductAdapter productAdapter;
    private ActionBar actionBar;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        name = intent.getStringExtra(Constant.PRODUCT_NAME);

        loadingDialog = new LoadingDialog(this);

        initToolBar();
        initServices();

        productAdapter = new ProductAdapter(this);
        productAdapter.setLoadingMoreListener(this);
        productAdapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);

        findViewById(R.id.btn_retry).setOnClickListener(this);

        fetchCategories();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name == null ? "\"\"" : "\"" + name + "\"");
        }
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        filterProductsService = Network.getInstance().createService(FilterProductsService.class);
        getCategoriesService = Network.getInstance().createService(GetCategoriesService.class);
        getProductService = Network.getInstance().createService(GetProductService.class);
        checkUserCartService = Network.getInstance().createService(CheckUserCartService.class);
    }

    private void showLoading() {
        hideError();
        recyclerView.setVisibility(View.INVISIBLE);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        avLoadingIndicatorView.setVisibility(View.INVISIBLE);
    }

    private void showError() {
        hideLoading();
        recyclerView.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorLayout.setVisibility(View.INVISIBLE);
    }

    private void addResult(PageResult<ProductReview> pageResult) {
        hideLoading();

        productAdapter.hideLoadingItem();

        productAdapter.addModels(pageResult.getResults(), false);

        if (productAdapter.getItemCount() == 0) {
            imageEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            imageEmpty.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (pageResult.getPageIndex() == pageResult.getTotalPages()) {
            productAdapter.disableLoadingMore(true);
        } else {
            productAdapter.disableLoadingMore(false);
        }
    }

    private void fetchCategories() {
        showLoading();
        compositeDisposable.add(getCategoriesService.getCategories()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGetCategoriesSuccess, this::onGetCategoriesFailed));
    }

    private void onGetCategoriesSuccess(List<Category> categories) {
        this.categories = categories;
        Category all = new Category();
        all.setName(getString(R.string.all));
        this.categories.add(0, all);
        searchView.setVisibility(View.VISIBLE);
        filterItem.setVisible(true);
        sortItem.setVisible(true);

        popupFilterMenu = new PopupMenu(this, findViewById(R.id.action_filter));
        for (int i = 0; i < categories.size(); i++) {
            popupFilterMenu.getMenu().add(Menu.NONE, i, Menu.NONE, categories.get(i).getName());
        }
        popupFilterMenu.setOnMenuItemClickListener(onFilterItemClickListener);

        popupSortMenu = new PopupMenu(this, findViewById(R.id.action_sort));
        popupSortMenu.inflate(R.menu.sort_pop_up_menu);
        popupSortMenu.setOnMenuItemClickListener(onSortItemClickListener);

        filterProducts(true);
    }


    private PopupMenu.OnMenuItemClickListener onFilterItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == 0) {
                category = null;
            } else {
                category = categories.get(item.getItemId()).getName();
            }
            filterProducts(true);
            return true;
        }
    };

    private PopupMenu.OnMenuItemClickListener onSortItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sort_created_date_desc: {
                    orderBy = Product.CREATED_DATE;
                    orderType = Constant.DESC;
                    filterProducts(true);
                    return true;
                }

                case R.id.sort_price_asc: {
                    orderBy = Product.CURRENT_PRICE;
                    orderType = Constant.ASC;
                    filterProducts(true);
                    return true;
                }

                case R.id.sort_price_desc: {
                    orderBy = Product.CURRENT_PRICE;
                    orderType = Constant.DESC;
                    filterProducts(true);
                    return true;
                }

                default: {
                    break;
                }
            }
            return false;
        }
    };

    private void onGetCategoriesFailed(Throwable e) {
        Log.i(TAG, "onGetCategoriesFailed: " + e);
        showError();
    }

    private void filterProducts(boolean isUpdate) {
        showLoading();
        imageEmpty.setVisibility(View.INVISIBLE);
        Log.i(TAG, "filterProducts: ");

        compositeDisposable.add(filterProductsService.filterProducts(name,
                category,
                0,
                10,
                orderBy,
                orderType)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> onFilterSuccess(success, isUpdate), this::onFilterFailed));
    }

    private void onFilterSuccess(PageResult<ProductReview> pageResult, boolean isUpdate) {
        if (isUpdate) {
            productAdapter.clear();
        }
        addResult(pageResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.input_key));

        filterItem = menu.findItem(R.id.action_filter);
        sortItem = menu.findItem(R.id.action_sort);

        searchView.setVisibility(View.INVISIBLE);
        filterItem.setVisible(false);
        sortItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
            break;

            case R.id.action_filter: {
                popupFilterMenu.show();
            }
            break;

            case R.id.action_sort: {
                popupSortMenu.show();
            }
            break;

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void onFilterFailed(Throwable e) {
        Log.i(TAG, "onFilterFailed: " + e);
        showError();
    }

    public void onRetry() {
        if (category == null) {
            fetchCategories();
        } else {
            filterProducts(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        actionBar.setTitle("\"" + name + "\"");
        this.name = query;
        this.category = null;
        this.orderBy = Product.CREATED_DATE;
        this.orderType = Constant.DESC;
        filterProducts(true);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onClick(View v) {
        showLoading();
        filterProducts(true);
    }

    @Override
    public void onLoadMore() {
        filterProducts(false);
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, int viewType, int position) {
        loadingDialog.show();
        ProductReview productReview = productAdapter.getItem(position, ProductReview.class);
        Observable<Product> productObservable = getProductService.getProduct(productReview.getId());
        Observable<Boolean> checkUserCartObservable = checkUserCartService.containProduct(UserAuth.getAuthUser().getAccessToken(), productReview.getId());

        compositeDisposable.add(Observable.combineLatest(productObservable, checkUserCartObservable, new BiFunction<Product, Boolean, Product>() {
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
}
