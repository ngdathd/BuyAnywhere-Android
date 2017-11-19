package com.uides.buyanywhere.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.ProductReview;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.GetProductReviewsService;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.utils.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class AllProductFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
    private static final String TAG = "AllProductFragment";
    public static final int LIMIT_PRODUCT = 5;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private AllProductAdapter allProductAdapter;
    private GetProductReviewsService getProductReviewsService;
    private CompositeDisposable compositeDisposable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, rootView);
        init();

        swipeRefreshLayout.setRefreshing(true);
        onRefresh();

        return rootView;
    }

    private void init() {
        Context context = getActivity();
        initAdapter(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(allProductAdapter);

        getProductReviewsService = Network.getInstance().createService(GetProductReviewsService.class);

        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    private void initAdapter(Context context) {
        allProductAdapter = new AllProductAdapter(context);

        int firstColor = getResources().getColor(R.color.colorAccent);
        int secondColor = getResources().getColor(R.color.colorPrimary);
        int thirdColor = getResources().getColor(R.color.colorPrimaryDark);
        swipeRefreshLayout.setColorSchemeColors(firstColor, secondColor, thirdColor);
        swipeRefreshLayout.setOnRefreshListener(this);

        allProductAdapter.setLoadingMoreListener(this);
    }

    @Override
    public void onRefresh() {
        Disposable disposable = getProductReviewsService
                .getProducts(1, LIMIT_PRODUCT, ProductReview.CREATED_DATE, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRefreshSuccess, this::onRefreshError);
        compositeDisposable.add(disposable);
    }

    private void onRefreshError(Throwable e) {
        Log.i(TAG, "onRefreshError: " + e);
        Toast.makeText(AllProductFragment.this.getActivity(),
                R.string.unexpected_error_message,
                Toast.LENGTH_SHORT)
                .show();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void onRefreshSuccess(PageResult<ProductReview> pageResult) {
        allProductAdapter.clear();
        allProductAdapter.disableLoadingMore(false);
        allProductAdapter.addModels(pageResult.getResults(), false);
        swipeRefreshLayout.setRefreshing(false);
        if (pageResult.getPageIndex() == pageResult.getTotalPages()) {
            allProductAdapter.disableLoadingMore(true);
        }
    }

    @Override
    public void onLoadMore() {
        allProductAdapter.showLoadingItem(true);
        Disposable disposable = getProductReviewsService
                .getProducts(allProductAdapter.getItemCount() / LIMIT_PRODUCT + 1, LIMIT_PRODUCT, ProductReview.CREATED_DATE, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onLoadMoreSuccess, this::onLoadMoreError);
        compositeDisposable.add(disposable);
    }

    private void onLoadMoreError(Throwable e) {
        allProductAdapter.hideLoadingItem();
    }

    private void onLoadMoreSuccess(PageResult<ProductReview> pageResult) {
        allProductAdapter.hideLoadingItem();
        allProductAdapter.addModels(pageResult.getResults(), false);
        if (pageResult.getPageIndex() == pageResult.getTotalPages()) {
            allProductAdapter.disableLoadingMore(true);
        }
    }

    private class AllProductAdapter extends EndlessLoadingRecyclerViewAdapter {
        AllProductAdapter(Context context) {
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
            Picasso.with(getActivity()).load(productReview.getPreviewUrl()).into(productViewHolder.imagePreview);
            productViewHolder.textName.setText(productReview.getName());
            productViewHolder.textShop.setText(productReview.getShopName());
            productViewHolder.textCurrentPrice.setText(productReview.getCurrentPrice() + " VND");
            productViewHolder.textOriginPrice.setText("" + productReview.getOriginPrice() + " VND");
            productViewHolder.textQuantity.append("" + productReview.getQuantity());
            productViewHolder.tagGroup.removeAll();
            Tag tag = new Tag(productReview.getCategoryName());
            tag.tagTextSize = 11;
            productViewHolder.tagGroup.addTag(tag);
            productViewHolder.ratingBar.setRating(productReview.getRating());
            productViewHolder.textTime.setText(DateUtil.getDateDiffNow(productReview.getCreatedDate()));
        }
    }

    class ProductViewHolder extends RecyclerViewAdapter.NormalViewHolder {
        @BindView(R.id.img_preview)
        ImageView imagePreview;
        @BindView(R.id.txt_name)
        TextView textName;
        @BindView(R.id.txt_shop)
        TextView textShop;
        @BindView(R.id.txt_current_price)
        TextView textCurrentPrice;
        @BindView(R.id.txt_origin_price)
        TextView textOriginPrice;
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