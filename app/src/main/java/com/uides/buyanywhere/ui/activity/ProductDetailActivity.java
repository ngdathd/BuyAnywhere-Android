package com.uides.buyanywhere.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.hedgehog.ratingbar.RatingBar;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.PriceTextView;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.custom_view.dialog.OrderDialog;
import com.uides.buyanywhere.custom_view.dialog.RatingDialog;
import com.uides.buyanywhere.custom_view.StrikeThroughPriceTextView;
import com.uides.buyanywhere.model.Feedback;
import com.uides.buyanywhere.model.Product;
import com.uides.buyanywhere.model.Rating;
import com.uides.buyanywhere.model.RatingResult;
import com.uides.buyanywhere.model.UserOrder;
import com.uides.buyanywhere.model.UserProfile;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.cart.AddCartService;
import com.uides.buyanywhere.service.cart.DeleteCartService;
import com.uides.buyanywhere.service.user.GetUserProfileService;
import com.uides.buyanywhere.service.user.RateProductService;
import com.uides.buyanywhere.ui.fragment.product.AllProductsFragment;
import com.uides.buyanywhere.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProductDetailActivity";
    private static final int LIST_FEEDBACK_REQUEST_CODE = 0;

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
    @BindView(R.id.btn_to_shop)
    Button toShopButton;
    @BindView(R.id.fab_shopping_cart)
    FloatingActionButton addToCartFab;
    @BindView(R.id.ln_feedback)
    LinearLayout feedbackLayout;
    @BindView(R.id.btn_view_more)
    Button viewMoreButton;
    private LayoutInflater inflater;

    private Product product;
    private MenuItem addToCartButton;
    private AddCartService addCartService;
    private DeleteCartService deleteCartService;
    private RateProductService rateProductService;
    private GetUserProfileService getUserProfileService;
    private CompositeDisposable compositeDisposable;
    private RatingDialog ratingDialog;
    private boolean isFromShopView;
    private boolean isViewByShopOwner;
    private OrderDialog orderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        inflater = LayoutInflater.from(this);
        ButterKnife.bind(this);
        initService();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            product = (Product) bundle.getSerializable(Constant.PRODUCT);
            isFromShopView = bundle.getBoolean(Constant.IS_FROM_SHOP, false);
            isViewByShopOwner = bundle.getBoolean(Constant.IS_VIEW_BY_SHOP_OWNER, false);
        }
        initViews();
        showViews(product);
    }

    private void initViews() {
        orderDialog = new OrderDialog(this, product.getQuantity());
        orderDialog.setOnSubmitSuccessListener((orderDialog, userOrder) -> {

        });
    }

    private void onOrderSuccess() {
        Toast.makeText(this, R.string.order_success, Toast.LENGTH_SHORT).show();
    }

    private void onOrderFailure(Throwable e) {
        Log.i(TAG, "onOrderFailure: " + e);
        Toast.makeText(this, R.string.order_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void initService() {
        compositeDisposable = new CompositeDisposable();
        Network network = Network.getInstance();
        addCartService = network.createService(AddCartService.class);
        deleteCartService = network.createService(DeleteCartService.class);
        rateProductService = network.createService(RateProductService.class);
        getUserProfileService = network.createService(GetUserProfileService.class);
    }

    private void showViews(Product product) {
        initToolBar();
        initCartButton();
        initImageSlider(product.getDescriptiveImageUrl());

        textName.setText(product.getName());
        textShop.setText(product.getShopName());
        textQuantity.setText("" + product.getQuantity());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.DAY_MONTH_YEAR, Locale.US);
        textCreatedDate.setText(simpleDateFormat.format(product.getCreatedDate()));
        long currentPrice = product.getCurrentPrice();
        textCurrentPrice.setPrice("" + currentPrice, Constant.PRICE_UNIT);
        long originPrice = product.getOriginPrice();
        if (currentPrice < originPrice) {
            int discountPercentage = (int) Math.floor((originPrice - currentPrice) * 1.0f / originPrice * 100);
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
        textDescription.setText(product.getDescription());

        if (isViewByShopOwner) {
            ratingButton.setVisibility(View.GONE);
            purchaseButton.setVisibility(View.GONE);
        } else {
            ratingButton.setOnClickListener(this);
            purchaseButton.setOnClickListener(this);
        }

        if (isFromShopView || isViewByShopOwner) {
            toShopButton.setVisibility(View.GONE);
        } else {
            toShopButton.setOnClickListener(this);
        }

        List<Feedback> previewFeedback = product.getPreviewFeedback();
        if (previewFeedback != null && !previewFeedback.isEmpty()) {
            if (product.getRatingCount() > AllProductsFragment.LATEST_FEEDBACK_COUNT) {
                viewMoreButton.setVisibility(View.VISIBLE);
            }
            viewMoreButton.setOnClickListener(this);
            for (Feedback fb : previewFeedback) {
                addLatestFeedBack(fb, -1);
            }
            viewMoreButton.setText(getString(R.string.view_more) + " (" + product.getRatingCount() + ")");
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(Constant.RATING, product.getRating());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void addLatestFeedBack(Feedback feedback, int index) {
        View view = inflater.inflate(R.layout.item_rating, feedbackLayout, false);
        Picasso.with(this)
                .load(feedback.getAvatarUrl())
                .placeholder(R.drawable.avatar_placeholder)
                .fit().into((CircleImageView) view.findViewById(R.id.img_avatar));
        ((TextView) view.findViewById(R.id.txt_name)).setText(feedback.getOwnerName());
        ((RatingBar) view.findViewById(R.id.rating_bar)).setStar(feedback.getRating());
        ((TextView) view.findViewById(R.id.txt_feedback)).setText(feedback.getComment());
        ((TextView) view.findViewById(R.id.txt_created_date)).setText(DateUtil.getDateDiffNow(feedback.getCreatedDate()));
        if (feedbackLayout.getChildCount() == AllProductsFragment.LATEST_FEEDBACK_COUNT) {
            if (index < AllProductsFragment.LATEST_FEEDBACK_COUNT - 1) {
                feedbackLayout.removeViewAt(AllProductsFragment.LATEST_FEEDBACK_COUNT - 1);
                feedbackLayout.addView(view, index);
            }
            viewMoreButton.setVisibility(View.VISIBLE);
        } else {
            feedbackLayout.addView(view, index);
        }
    }

    private void initToolBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initCartButton() {
        if (isViewByShopOwner) {
            addToCartFab.setVisibility(View.GONE);
        } else {
            addToCartFab.setOnClickListener(this);

            if (product.isAddedToCart()) {
                addToCartFab.setImageResource(R.drawable.ic_added_to_cart);
            }
        }
    }

    private void initImageSlider(List<String> imageUrls) {
        if (imageUrls == null) {
            return;
        }
        for (String imageUrl : imageUrls) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.image(imageUrl)
                    .setScaleType(BaseSliderView.ScaleType.Fit);
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

            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
            collapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(Color.TRANSPARENT));
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_detail_tool_bar, menu);
        addToCartButton = menu.findItem(R.id.action_add_to_cart);
        if (isViewByShopOwner) {
            addToCartButton.setVisible(false);
        } else if (product.isAddedToCart()) {
            addToCartButton.setIcon(R.drawable.ic_added_to_cart);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
            break;

            case R.id.action_add_to_cart: {
                if (product.isAddedToCart()) {
                    removeProductFromCart(product);
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
                    removeProductFromCart(product);
                } else {
                    addProductToCart(product);
                }
            }
            break;

            case R.id.btn_rating: {
                ratingDialog = new RatingDialog(this)
                        .setOnPositiveButtonClickListener((rating, textFeedback) -> {
                            Rating ratingModel = new Rating();
                            ratingModel.setRating(rating);
                            ratingModel.setComment(textFeedback);

                            compositeDisposable.add(
                                    rateProductService.rateProduct(UserAuth.getAuthUser().getAccessToken(),
                                            product.getId(),
                                            ratingModel)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(this::onRatingSuccess, this::onRatingFailed)
                            );
                            return true;
                        });
                ratingDialog.show();
            }
            break;

            case R.id.btn_purchase: {
                compositeDisposable.add(getUserProfileService.getUserProfile(UserAuth.getAuthUser().getAccessToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(this::onFetchUserProfileSuccess, this::onFetchUserProfileFailure));

                orderDialog.showProgressBar(true);
                orderDialog.show();
            }
            break;

            case R.id.btn_to_shop: {
                Intent intent = new Intent(this, ShopActivity.class);
                intent.putExtra(Constant.SHOP_ID, product.getShopID());
                startActivity(intent);
            }
            break;

            case R.id.btn_view_more: {
                Intent intent = new Intent(this, ListFeedbackActivity.class);
                intent.putExtra(Constant.PRODUCT_ID, product.getId());
                startActivityForResult(intent, LIST_FEEDBACK_REQUEST_CODE);
            }
            break;

            default: {
                break;
            }
        }
    }

    private void onFetchUserProfileSuccess(UserProfile userProfile) {
        orderDialog.showData(userProfile);
        orderDialog.showProgressBar(false);
    }

    private void onFetchUserProfileFailure(Throwable e) {
        Log.i(TAG, "onFetchUserProfileFailure: ");
        orderDialog.dismiss();
        Toast.makeText(this, R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LIST_FEEDBACK_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    RatingResult ratingResult = (RatingResult) data.getSerializableExtra(Constant.RATING_RESULT);
                    product.setRating(ratingResult.getAverageRating());
                    product.setRatingCount(ratingResult.getRatingCount());

                    textRatingScore.setText("" + product.getRating());
                    textRatingCount.setText("" + product.getRatingCount());
                    addLatestFeedBack(ratingResult.getUserRating(), 0);

                    viewMoreButton.setText(getString(R.string.view_more) + " (" + product.getRatingCount() + ")");
                }
            }
            break;

            default: {
                break;
            }
        }
    }

    private void onRatingSuccess(RatingResult ratingResult) {
        ratingDialog.dismiss();
        product.setRatingCount(ratingResult.getRatingCount());
        product.setRating(ratingResult.getAverageRating());
        ratingBar.setStar(product.getRating());
        textRatingCount.setText("" + product.getRatingCount());
        textRatingScore.setText("" + product.getRating());
        addLatestFeedBack(ratingResult.getUserRating(), 0);

        viewMoreButton.setText(getString(R.string.view_more) + " (" + product.getRatingCount() + ")");

        Intent intent = new Intent();
        intent.putExtra(Constant.RATING, ratingResult.getAverageRating());
        setResult(RESULT_OK, intent);

        Log.i(TAG, "onRatingSuccess: " + ratingResult);
        Toast.makeText(ProductDetailActivity.this,
                R.string.feedback_sent,
                Toast.LENGTH_SHORT).show();
    }

    private void onRatingFailed(Throwable e) {
        ratingDialog.dismiss();
        Log.i(TAG, "onRatingFailed: " + e);
        Toast.makeText(ProductDetailActivity.this,
                R.string.unexpected_error_message,
                Toast.LENGTH_SHORT).show();
    }

    private void removeProductFromCart(Product product) {
        Disposable disposable = deleteCartService
                .deleteProductFromCart(UserAuth.getAuthUser().getAccessToken(), product.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object -> onRemoveProductFromCartSuccess(), this::onCartRequestFailed);
        compositeDisposable.add(disposable);
    }

    private void addProductToCart(Product product) {
        Disposable disposable = addCartService
                .addProductToCart(UserAuth.getAuthUser().getAccessToken(), product.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object -> onAddProductToCartSuccess(), this::onCartRequestFailed);
        compositeDisposable.add(disposable);
    }

    private void onCartRequestFailed(Throwable e) {
        Log.i(TAG, "onCartRequestFailed: " + e);
        Toast.makeText(this, R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    void onAddProductToCartSuccess() {
        product.setAddedToCart(true);
        Toast.makeText(this, R.string.add_product_success, Toast.LENGTH_SHORT).show();
        addToCartButton.setIcon(R.drawable.ic_added_to_cart);
        addToCartFab.setImageResource(R.drawable.ic_added_to_cart);
    }

    void onRemoveProductFromCartSuccess() {
        product.setAddedToCart(false);
        Toast.makeText(this, R.string.remove_product_success, Toast.LENGTH_SHORT).show();
        addToCartButton.setIcon(R.drawable.ic_add_to_cart);
        addToCartFab.setImageResource(R.drawable.ic_add_to_cart);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constant.CART_REMOVED, !product.isAddedToCart());
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }
}
