package com.uides.buyanywhere.ui.fragment.cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.PriceTextView;
import com.uides.buyanywhere.custom_view.StrikeThroughPriceTextView;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Product;
import com.uides.buyanywhere.model.ProductReview;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.cart.DeleteCartService;
import com.uides.buyanywhere.service.cart.GetCartService;
import com.uides.buyanywhere.service.product.GetProductService;
import com.uides.buyanywhere.ui.activity.ProductDetailActivity;
import com.uides.buyanywhere.ui.fragment.RecyclerViewFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class ShoppingCartFragment extends RecyclerViewFragment implements RecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "ShoppingCartFragment";
    private static final int PRODUCT_DETAIL_REQUEST_CODE = 1;

    private GetCartService getCartService;
    private DeleteCartService deleteCartService;
    private LoadingDialog loadingDialog;
    private GetProductService getProductService;
    private boolean hasItemDeleting = false;
    private int detailProductPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadingDialog = new LoadingDialog(getActivity());
        initService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:{

            }
            break;

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initService() {
        Network network = Network.getInstance();
        getCartService = network.createService(GetCartService.class);
        deleteCartService = network.createService(DeleteCartService.class);
        getProductService = network.createService(GetProductService.class);
    }

    @Override
    protected RecyclerViewAdapter initAdapter() {
        ShoppingCartAdapter shoppingCartAdapter = new ShoppingCartAdapter(getActivity());
        shoppingCartAdapter.setOnItemClickListener(this);
        return shoppingCartAdapter;
    }

    @Override
    public void onRefresh() {
        Disposable disposable = getCartService
                .getProductsInCart(UserAuth.getAuthUser().getAccessToken(),
                        null,
                        null,
                        ProductReview.CREATED_DATE,
                        1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRefreshSuccess, this::onRefreshError);
        addDisposable(disposable);
    }

    private void onRefreshError(Throwable e) {
        Log.i(TAG, "onRefreshError: " + e);
        Toast.makeText(ShoppingCartFragment.this.getActivity(),
                R.string.unexpected_error_message,
                Toast.LENGTH_SHORT)
                .show();
        getSwipeRefreshLayout().setRefreshing(false);
    }

    private void onRefreshSuccess(PageResult<ProductReview> pageResult) {
        ShoppingCartAdapter shoppingCartAdapter = (ShoppingCartAdapter) getAdapter();
        shoppingCartAdapter.clear();
        shoppingCartAdapter.addCarts(pageResult.getResults(), false);
        getSwipeRefreshLayout().setRefreshing(false);
        if(shoppingCartAdapter.getItemCount() == 0) {
            showEmptyImage(true);
        } else {
            showEmptyImage(false);
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, int viewType, int position) {
        CartWrapper cartWrapper = getAdapter().getItem(position, CartWrapper.class);
        if (cartWrapper.isDeleting) {
            return;
        }
        loadingDialog.show();
        Disposable disposable = getProductService.getProduct(cartWrapper.productReview.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(product -> onFetchProductSuccess(position, product), this::onFetchProductFailed);
        addDisposable(disposable);
    }

    private void onFetchProductSuccess(int position, Product product) {
        loadingDialog.dismiss();
        detailProductPosition = position;
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        Bundle bundle = new Bundle();
        product.setAddedToCart(true);
        bundle.putSerializable(Constant.PRODUCT, product);
        boolean userIsShopOwner = UserAuth.getAuthUser().getShopID().equals(product.getShopID());
        bundle.putBoolean(Constant.IS_VIEW_BY_SHOP_OWNER, userIsShopOwner);
        intent.putExtras(bundle);
        startActivityForResult(intent, PRODUCT_DETAIL_REQUEST_CODE);
    }

    private void onFetchProductFailed(Throwable e) {
        loadingDialog.dismiss();
        Log.i(TAG, "onFetchProductFailed: " + e);
        Toast.makeText(getActivity(), R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    private class ShoppingCartAdapter extends RecyclerViewAdapter {

        public ShoppingCartAdapter(Context context) {
            super(context, false);
        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View shoppingCartView = getInflater().inflate(R.layout.item_cart, parent, false);
            return new ShoppingCartViewHolder(shoppingCartView);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            ShoppingCartViewHolder shoppingCartViewHolder = (ShoppingCartViewHolder) holder;

            CartWrapper cartWrapper = getItem(position, CartWrapper.class);
            ProductReview productReview = cartWrapper.productReview;

            Picasso.with(getActivity())
                    .load(productReview.getPreviewUrl())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(shoppingCartViewHolder.imagePreview);

            shoppingCartViewHolder.textName.setText(productReview.getName());
            shoppingCartViewHolder.textShop.setText(productReview.getShopName());
            shoppingCartViewHolder.textQuantity.setText("" + productReview.getQuantity());
            long currentPrice = productReview.getCurrentPrice();
            long originPrice = productReview.getCurrentPrice();
            if (currentPrice < originPrice) {
                shoppingCartViewHolder.textCurrentPrice.setPrice("" + currentPrice, Constant.PRICE_UNIT);
                shoppingCartViewHolder.textOriginPrice.setVisibility(View.VISIBLE);
                shoppingCartViewHolder.textOriginPrice.setPrice("" + originPrice, Constant.PRICE_UNIT);
            } else {
                shoppingCartViewHolder.textCurrentPrice.setPrice("" + originPrice, Constant.PRICE_UNIT);
                shoppingCartViewHolder.textOriginPrice.setVisibility(View.INVISIBLE);
            }
            shoppingCartViewHolder.tagView.removeAll();
            Tag tag = new Tag(productReview.getCategoryName());
            tag.tagTextSize = 11;
            shoppingCartViewHolder.tagView.addTag(tag);

            if (cartWrapper.isDeleting) {
                shoppingCartViewHolder.progressDelete.setVisibility(View.VISIBLE);
                shoppingCartViewHolder.deleteButton.setVisibility(View.INVISIBLE);
                shoppingCartViewHolder.buttonPurchase.setEnabled(false);
            } else {
                shoppingCartViewHolder.progressDelete.setVisibility(View.INVISIBLE);
                shoppingCartViewHolder.deleteButton.setVisibility(View.VISIBLE);
                shoppingCartViewHolder.buttonPurchase.setEnabled(true);
            }
        }

        public void addCarts(List<ProductReview> carts, boolean isScroll) {
            for (ProductReview productReview : carts) {
                addModel(new CartWrapper(productReview), false, false);
            }
            notifyDataSetChanged();
            if (isScroll) {
                getRecyclerView().scrollToPosition(carts.size() - 1);
            }
        }
    }

    private class CartWrapper {
        ProductReview productReview;
        boolean isDeleting;

        public CartWrapper(ProductReview productReview) {
            this.productReview = productReview;
            this.isDeleting = false;
        }
    }

    public class ShoppingCartViewHolder extends RecyclerViewAdapter.NormalViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_name)
        TextView textName;
        @BindView(R.id.txt_shop_name)
        TextView textShop;
        @BindView(R.id.txt_quantity)
        TextView textQuantity;
        @BindView(R.id.txt_current_price)
        PriceTextView textCurrentPrice;
        @BindView(R.id.txt_origin_price)
        StrikeThroughPriceTextView textOriginPrice;
        @BindView(R.id.tag_group)
        TagView tagView;
        @BindView(R.id.btn_purchase)
        Button buttonPurchase;
        @BindView(R.id.btn_delete)
        Button deleteButton;
        @BindView(R.id.img_preview)
        ImageView imagePreview;
        @BindView(R.id.progress_delete)
        ProgressBar progressDelete;

        public ShoppingCartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            buttonPurchase.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_purchase: {

                }
                break;

                case R.id.btn_delete: {
                    if (hasItemDeleting) {
                        return;
                    }

                    int position = getAdapterPosition();

                    RecyclerViewAdapter adapter = getAdapter();
                    CartWrapper cartWrapper = adapter.getItem(position, CartWrapper.class);
                    cartWrapper.isDeleting = true;

                    hasItemDeleting = true;
                    getSwipeRefreshLayout().setEnabled(false);

                    Disposable disposable = deleteCartService.deleteProductFromCart(UserAuth.getAuthUser().getAccessToken(),
                            cartWrapper.productReview.getId())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(success -> onDeleteSuccess(position), error -> onDeleteFailed(position, error));
                    addDisposable(disposable);

                    adapter.notifyItemChanged(position);
                }
                break;

                default: {
                    break;
                }
            }
        }

        private void onDeleteSuccess(int position) {
            CartWrapper cartWrapper = getAdapter().getItem(position, CartWrapper.class);
            cartWrapper.isDeleting = false;

            hasItemDeleting = false;
            getSwipeRefreshLayout().setEnabled(true);

            getAdapter().removeModel(position);

            if(getAdapter().getItemCount() == 0) {
                showEmptyImage(true);
            } else {
                showEmptyImage(false);
            }
        }

        private void onDeleteFailed(int position, Throwable e) {
            CartWrapper cartWrapper = getAdapter().getItem(position, CartWrapper.class);
            cartWrapper.isDeleting = false;

            hasItemDeleting = false;
            getSwipeRefreshLayout().setEnabled(true);

            getAdapter().notifyItemChanged(position);

            Log.i(TAG, "onDeleteFailed: " + e);
            Toast.makeText(getActivity(), R.string.delete_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PRODUCT_DETAIL_REQUEST_CODE: {
                if (data.getBooleanExtra(Constant.CART_REMOVED, false)) {
                    RecyclerViewAdapter recyclerViewAdapter = getAdapter();
                    recyclerViewAdapter.removeModel(detailProductPosition);
                    if(recyclerViewAdapter.getItemCount() == 0) {
                        showEmptyImage(true);
                    } else {
                        showEmptyImage(false);
                    }
                }
            }
            break;
        }
    }
}
