package com.uides.buyanywhere.ui.activity;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.hedgehog.ratingbar.RatingBar;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.custom_view.PriceTextView;
import com.uides.buyanywhere.custom_view.RatingDialog;
import com.uides.buyanywhere.custom_view.StrikeThroughPriceTextView;
import com.uides.buyanywhere.model.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class ProductDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, View.OnClickListener {
    @BindView(R.id.image_slider)
    SliderLayout imageSlider;
    @BindView(R.id.txt_name)
    TextView textName;
    @BindView(R.id.txt_shop_name)
    TextView textShop;
    @BindView(R.id.txt_quantity)
    TextView textQuantity;
    @BindView(R.id.txt_created_date)
    TextView textCreatedDate;
    @BindView(R.id.txt_current_price)
    PriceTextView textCurrentPrice;
    @BindView(R.id.txt_origin_price)
    StrikeThroughPriceTextView textOriginPrice;
    @BindView(R.id.txt_discount_percentage)
    TextView textDiscountPercentage;
    @BindView(R.id.rating_bar)
    RatingBar ratingBar;
    @BindView(R.id.txt_rating_score)
    TextView textRatingScore;
    @BindView(R.id.txt_rating_count)
    TextView textRatingCount;
    @BindView(R.id.txt_description)
    TextView textDescription;
    @BindView(R.id.img_discount)
    ImageView imageDiscount;
    @BindView(R.id.btn_rating)
    Button ratingButton;
    @BindView(R.id.btn_purchase)
    Button purchaseButton;

    private Product product;
    private FloatingActionButton addToCartFab;
    private MenuItem addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            product = (Product) bundle.getSerializable(Constant.PRODUCT);
        }

        initToolBar();
        initFloatingButton();
        initImageSlider(null);
        initViews();
    }

    private void initViews() {
        textName.setText(product.getName());
        textShop.setText(product.getShopName());
        textQuantity.setText("" + product.getQuantity());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.DAY_MONTH_YEAR, Locale.US);
        textCreatedDate.setText(simpleDateFormat.format(product.getCreatedDate()));
        long currentPrice = product.getCurrentPrice();
        textCurrentPrice.setPrice("" + currentPrice, Constant.PRICE_UNIT);
        long originPrice = product.getOriginPrice();
        if (currentPrice < originPrice) {
            int discountPercentage = Math.round((originPrice - currentPrice) * 1.0f / originPrice * 100);
            textOriginPrice.setPrice("" + originPrice, Constant.PRICE_UNIT);
            textDiscountPercentage.setText("-" + discountPercentage + "%");
        } else {
            textOriginPrice.setVisibility(View.INVISIBLE);
            textDiscountPercentage.setVisibility(View.INVISIBLE);
            imageDiscount.setVisibility(View.INVISIBLE);
        }
        float productRating = product.getRating();
        ratingBar.setStar(productRating);
        textRatingScore.setText("" + productRating);
        textRatingCount.setText("" + product.getRatingCount());
        //TODO: show product description
//        textDescription.setText(product.getDescription());

        ratingButton.setOnClickListener(this);
        purchaseButton.setOnClickListener(this);
    }

    private void initToolBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addToCartButton = toolbar.getMenu().findItem(R.id.action_add_to_cart);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initFloatingButton() {
        addToCartFab = (FloatingActionButton) findViewById(R.id.fab_shopping_cart);
        addToCartFab.setOnClickListener(this);

        if (product.isAddedToCart()) {
            addToCartFab.setImageResource(R.drawable.ic_added_to_cart);
        }
    }

    private void initImageSlider(List<String> previewImageUrls) {
        previewImageUrls = new ArrayList<>();
        previewImageUrls.add("https://v2.tikicdn.com/cache/550x550/media/catalog/product/1/_/1.u4939.d20170918.t113145.497297_4_3_3_1.jpg");
        previewImageUrls.add("https://v2.tikicdn.com/cache/550x550/media/catalog/product/2/_/2.u4939.d20170918.t113145.543503_4_3_3_1.jpg");
        previewImageUrls.add("https://v2.tikicdn.com/cache/550x550/media/catalog/product/3/_/3.u4939.d20170918.t113145.578791_4_3_3_1.jpg");
        previewImageUrls.add("https://v2.tikicdn.com/cache/w550/media/catalog/product/x/7/x7.u4939.d20170918.t113212.626011_4_3_3.jpg");
        previewImageUrls.add("https://v2.tikicdn.com/cache/w550/media/catalog/product/x/9/x9.u4939.d20170918.t113212.672806_4_3_3.jpg");
        previewImageUrls.add("https://v2.tikicdn.com/cache/w550/media/catalog/product/x/1/x11.u4939.d20170918.t113212.749068_4_3_3.jpg");

        for (String imageUrl : previewImageUrls) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.image(imageUrl)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            imageSlider.addSlider(textSliderView);
        }

        imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new DescriptionAnimation());
        imageSlider.setDuration(Constant.SLIDE_DURATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            CollapsingToolbarLayout collapsing_toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
            collapsing_toolbar_layout.setExpandedTitleTextColor(ColorStateList.valueOf(Color.TRANSPARENT));
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_detail_tool_bar, menu);
        addToCartButton = menu.findItem(R.id.action_add_to_cart);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_to_cart: {
                if (product.isAddedToCart()) {
                    removeProductFromToCart(product);
                } else {
                    addProductToCart(product);
                }
            }
            break;

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_shopping_cart: {
                if (product.isAddedToCart()) {
                    removeProductFromToCart(product);
                } else {
                    addProductToCart(product);
                }
            }
            break;

            case R.id.btn_rating: {
                new RatingDialog(this)
                        .setOnPositiveButtonClickListener((rating, textFeedback) -> {
                            //TODO send rating
                            Toast.makeText(ProductDetailActivity.this,
                                    R.string.feedback_sent,
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }).show();
            }
            break;

            case R.id.btn_purchase: {
                //TODO purchasing product logic
            }
            break;

            default: {
                break;
            }
        }
    }

    private void removeProductFromToCart(Product product) {
        //TODO: remove product from cart
        onRemoveProductFromCartSuccess();
    }

    private void addProductToCart(Product product) {
        //TODO: add product to cart
        onAddProductToCartSuccess();
    }

    void onAddProductToCartSuccess() {
        product.setAddedToCart(true);
        Toast.makeText(this, R.string.add_product_success, Toast.LENGTH_SHORT).show();
        ;
        addToCartButton.setIcon(R.drawable.ic_added_to_cart);
        addToCartFab.setImageResource(R.drawable.ic_added_to_cart);
    }

    void onRemoveProductFromCartSuccess() {
        product.setAddedToCart(false);
        Toast.makeText(this, R.string.remove_product_success, Toast.LENGTH_SHORT).show();
        ;
        addToCartButton.setIcon(R.drawable.ic_add_to_cart);
        addToCartFab.setImageResource(R.drawable.ic_add_to_cart);
    }
}
