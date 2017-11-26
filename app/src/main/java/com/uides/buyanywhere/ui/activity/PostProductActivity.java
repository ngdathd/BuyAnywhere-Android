package com.uides.buyanywhere.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.esafirm.imagepicker.model.Image;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.categories.GetCategoriesService;
import com.uides.buyanywhere.ui.fragment.shop.ChildPostProductFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public class PostProductActivity extends LoadingActivity {
    private static final String TAG = "PostProductActivity";
    private CompositeDisposable compositeDisposable;
    private GetCategoriesService getCategoriesService;

    private ChildPostProductFragment childPostProductFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        childPostProductFragment = new ChildPostProductFragment();
        initServices();
        showLoadingFragment();
        fetchCategories();
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        Network network = Network.getInstance();
        getCategoriesService = network.createService(GetCategoriesService.class);
    }

    private void fetchCategories() {
        compositeDisposable.add(getCategoriesService.getCategories()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchCategoriesSuccess, this::onFetchCategoriesFailed));
    }

    private void onFetchCategoriesSuccess(List<Category> categories) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constant.CATEGORIES, (ArrayList<? extends Parcelable>) categories);
        childPostProductFragment.setArguments(bundle);
        showFragment(childPostProductFragment);
    }

    private void onFetchCategoriesFailed(Throwable e) {
        Log.i(TAG, "onFetchCategoriesFailed: " + e);
        showErrorScreen();
    }

    @Override
    public void onRetry() {
        super.onRetry();
        fetchCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.post_product);
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }
}
