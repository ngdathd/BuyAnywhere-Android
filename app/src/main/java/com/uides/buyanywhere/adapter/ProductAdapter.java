package com.uides.buyanywhere.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.hedgehog.ratingbar.RatingBar;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.custom_view.PriceTextView;
import com.uides.buyanywhere.custom_view.StrikeThroughPriceTextView;
import com.uides.buyanywhere.model.ProductPreview;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.utils.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 27/11/2017.
 */

public class ProductAdapter extends EndlessLoadingRecyclerViewAdapter {
    private Context context;

    public ProductAdapter(Context context) {
        super(context, false);
        this.context = context;
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
        Picasso.with(context)
                .load(productPreview.getPreviewUrl())
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .centerCrop()
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

    public class ProductViewHolder extends RecyclerViewAdapter.NormalViewHolder {
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
