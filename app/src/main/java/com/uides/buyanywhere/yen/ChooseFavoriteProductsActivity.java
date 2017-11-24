package com.uides.buyanywhere.yen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.ProductReview;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.CategoriesService;
import com.uides.buyanywhere.service.GetProductReviewsService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yen on 14/11/2017.
 */

public class ChooseFavoriteProductsActivity extends AppCompatActivity {
    private static final String TAG = "ChooseFavoriteProducts";//251782064
    @BindView(R.id.list_item_favor_product)
    RecyclerView favorProduct;

    private CompositeDisposable compositeDisposable;
    private CategoriesService categoriesService;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_choosing_favorite_product);
        ButterKnife.bind(this);

        compositeDisposable = new CompositeDisposable();
        categoriesService = Network.getInstance().createService(CategoriesService.class);
        Disposable disposable = categoriesService
                .getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onSuccess, this::onRefreshError);
        compositeDisposable.add(disposable);

        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, false);
        favorProduct.setLayoutManager(new GridLayoutManager(this, 3));
        favorProduct.setAdapter(myRecyclerViewAdapter);
    }

    private void onSuccess(List<Category> categories) {
        myRecyclerViewAdapter.addModels(categories, false);
        Log.i(TAG, categories.toString()); // vẫn số
    }

    private void onRefreshError(Throwable e) {
        Log.i(TAG, "onRefreshError: " + e);
        Toast.makeText(ChooseFavoriteProductsActivity.this,
                R.string.unexpected_error_message,
                Toast.LENGTH_SHORT)
                .show();
    }

    private class MyRecyclerViewAdapter extends RecyclerViewAdapter {
        public MyRecyclerViewAdapter(Context context, boolean enableSelectedMode) {
            super(context, enableSelectedMode);
        }
        //hết lỗi chưa c rồi, nhưng sao hàm đó k dùng ư?
        //c call service ở đâu? service của cái này à? uk gọi mỗi api thôi uk, thế ở đâu v

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View itemView = getInflater().inflate(R.layout.item_choosing_favorite_product, parent, false);
            return new ItemViewHolder(itemView);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            Category category = getItem(position, Category.class);
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            Picasso.with(getApplicationContext()).load(category.getIconUrl()).into(viewHolder.product);
            viewHolder.name.setText(category.getName());
        }
    }

    private class ItemViewHolder extends RecyclerViewAdapter.NormalViewHolder {
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
