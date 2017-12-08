package com.uides.buyanywhere.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hedgehog.ratingbar.RatingBar;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.dialog.RatingDialog;
import com.uides.buyanywhere.model.Feedback;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Rating;
import com.uides.buyanywhere.model.RatingResult;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.rating.GetFeedbackService;
import com.uides.buyanywhere.service.user.RateProductService;
import com.uides.buyanywhere.utils.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 02/12/2017.
 */

public class ListFeedbackActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "PostProductActivity";

    private RatingResult ratingResult;
    private static final Integer LIMIT_FEEDBACK = 10;

    @BindView(R.id.btn_rating)
    Button buttonRating;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.img_empty)
    ImageView imageEmpty;

    private CompositeDisposable compositeDisposable;
    private GetFeedbackService getFeedbackService;
    private RateProductService rateProductService;
    private RatingDialog ratingDialog;

    private String productID;
    private FeedbackAdapter feedbackAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_feedback);
        ButterKnife.bind(this);
        initServices();
        productID = getIntent().getStringExtra(Constant.PRODUCT_ID);

        feedbackAdapter = new FeedbackAdapter(this);

        initToolBar();
        initViews();

        if (feedbackAdapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.feedback);
        }
    }

    private void initViews() {
        buttonRating.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(feedbackAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        int firstColor = getResources().getColor(R.color.blue);
        int secondColor = getResources().getColor(R.color.red);
        int thirdColor = getResources().getColor(R.color.yellow);
        int fourthColor = getResources().getColor(R.color.green);
        swipeRefreshLayout.setColorSchemeColors(firstColor, secondColor, thirdColor, fourthColor);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        Network network = Network.getInstance();
        getFeedbackService = network.createService(GetFeedbackService.class);
        rateProductService = network.createService(RateProductService.class);
    }

    @Override
    public void onRefresh() {
        feedbackAdapter.disableLoadingMore(true);
        compositeDisposable.add(getFeedbackService.getAllFeedback(productID,
                0,
                LIMIT_FEEDBACK,
                Feedback.CREATED_DATE,
                Constant.DESC)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRefreshFeedbackSuccess, this::onRefreshFeedbackFailed));
    }

    private void onRefreshFeedbackSuccess(PageResult<Feedback> feedback) {
        swipeRefreshLayout.setRefreshing(false);
        feedbackAdapter.disableLoadingMore(false);
        feedbackAdapter.clear();
        feedbackAdapter.addModels(feedback.getResults(), false);

        if (feedback.getPageIndex() == feedback.getTotalPages()) {
            feedbackAdapter.disableLoadingMore(true);
        }

        if (feedbackAdapter.getItemCount() == 0) {
            imageEmpty.setVisibility(View.VISIBLE);
        } else {
            imageEmpty.setVisibility(View.INVISIBLE);
        }
    }

    private void onRefreshFeedbackFailed(Throwable e) {
        Log.i(TAG, "onRefreshFeedbackFailed: " + e);
        swipeRefreshLayout.setRefreshing(false);
        feedbackAdapter.disableLoadingMore(false);
    }

    @Override
    public void finish() {
        if (ratingResult != null) {
            Intent intent = new Intent();
            intent.putExtra(Constant.RATING_RESULT, ratingResult);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rating: {
                ratingDialog = new RatingDialog(this)
                        .setOnPositiveButtonClickListener((rating, textFeedback) -> {
                            Rating ratingModel = new Rating();
                            ratingModel.setRating(rating);
                            ratingModel.setComment(textFeedback);

                            compositeDisposable.add(
                                    rateProductService.rateProduct(UserAuth.getAuthUser().getAccessToken(),
                                            productID,
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

            default: {
                break;
            }
        }
    }

    private void onRatingSuccess(RatingResult ratingResult) {
        buttonRating.setVisibility(View.GONE);
        ratingDialog.dismiss();
        this.ratingResult = ratingResult;
        feedbackAdapter.addModel(0, ratingResult.getUserRating(), false);
        Log.i(TAG, "onRatingSuccess: " + ratingResult);
        Toast.makeText(this,
                R.string.feedback_sent,
                Toast.LENGTH_SHORT).show();
    }

    private void onRatingFailed(Throwable e) {
        ratingDialog.dismiss();
        Log.i(TAG, "onRatingFailed: " + e);
        Toast.makeText(this,
                R.string.unexpected_error_message,
                Toast.LENGTH_SHORT).show();
    }

    private class FeedbackAdapter extends EndlessLoadingRecyclerViewAdapter {

        public FeedbackAdapter(Context context) {
            super(context, false);
        }

        @Override
        protected RecyclerView.ViewHolder initLoadingViewHolder(ViewGroup parent) {
            View view = getInflater().inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

        @Override
        protected void bindLoadingViewHolder(LoadingViewHolder loadingViewHolder, int position) {

        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View view = getInflater().inflate(R.layout.item_rating, parent, false);
            return new FeedbackViewHolder(view);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            FeedbackViewHolder feedbackViewHolder = (FeedbackViewHolder) holder;
            Feedback feedback = getItem(position, Feedback.class);
            Picasso.with(ListFeedbackActivity.this)
                    .load(feedback.getAvatarUrl())
                    .placeholder(R.drawable.avatar_placeholder)
                    .fit().into(feedbackViewHolder.avatarImage);
            feedbackViewHolder.textName.setText(feedback.getOwnerName());
            feedbackViewHolder.textFeedback.setText(feedback.getComment());
            feedbackViewHolder.ratingBar.setStar(feedback.getRating());
            feedbackViewHolder.textCreatedDate.setText(DateUtil.getDateDiffNow(feedback.getCreatedDate()));
        }
    }

    public class FeedbackViewHolder extends RecyclerViewAdapter.NormalViewHolder {
        @BindView(R.id.img_avatar)
        CircleImageView avatarImage;
        @BindView(R.id.txt_name)
        TextView textName;
        @BindView(R.id.txt_feedback)
        TextView textFeedback;
        @BindView(R.id.rating_bar)
        RatingBar ratingBar;
        @BindView(R.id.txt_created_date)
        TextView textCreatedDate;

        public FeedbackViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
