package com.uides.buyanywhere.yen;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yen on 14/11/2017.
 */

public class ChooseFavoriteProductsActivity extends AppCompatActivity {
    @BindView(R.id.list_item_favor_product)
    RecyclerView favorProduct;

    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_choosing_favorite_product);
        ButterKnife.bind(this);

        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, false);
        favorProduct.setLayoutManager(new LinearLayoutManager(this));
        favorProduct.setAdapter(myRecyclerViewAdapter);

//        initNavigationBar();
    }

    private class MyRecyclerViewAdapter extends RecyclerViewAdapter {
        public MyRecyclerViewAdapter(Context context, boolean enableSelectedMode) {
            super(context, enableSelectedMode);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View itemView = inflater.inflate(R.layout.layout_choosing_favorite_product, parent, false);
            return new NormalViewHolder(itemView);
        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View itemView = getInflater().inflate(R.layout.item_choosing_favorite_product, parent, false);
            return new ItemViewHolder(itemView);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Category category = getItem(position, Category.class);
            itemViewHolder.name.setText(category.getName());
//            itemViewHolder.ok
            Picasso.with(getInflater().getContext())
                    .load(category.getIconUrl())
                    .into(itemViewHolder.product);

        }
    }

    private class ItemViewHolder extends EndlessLoadingRecyclerViewAdapter.NormalViewHolder {
        private ImageView product;
        private ImageView ok;
        private TextView name;

        public ItemViewHolder(View itemView) {
            super(itemView);

            product = (ImageView) itemView.findViewById(R.id.product_icon);
            ok = (ImageView) itemView.findViewById(R.id.is_ok);
            name = (TextView) itemView.findViewById(R.id.product_name);

        }
    }
}
