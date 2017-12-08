package com.uides.buyanywhere.ui.fragment.shop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hedgehog.ratingbar.RatingBar;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.model.Shop;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.user.GetOwnerShop;
import com.uides.buyanywhere.ui.activity.ShopEditActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 25/11/2017.
 */

public class ShopInformationFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ShopInformationFragment";
    private static final int SHOP_EDIT_REQUEST_CODE = 0;

    @BindView(R.id.txt_shop_name)
    TextView textName;
    @BindView(R.id.txt_shop_address)
    TextView textAddress;
    @BindView(R.id.txt_phone)
    TextView textPhone;
    @BindView(R.id.rl_email)
    RelativeLayout emailGroup;
    @BindView(R.id.txt_email)
    TextView textEmail;
    @BindView(R.id.rl_website)
    RelativeLayout websiteGroup;
    @BindView(R.id.txt_website)
    TextView textWebsite;
    @BindView(R.id.rl_facebook)
    RelativeLayout facebookGroup;
    @BindView(R.id.txt_facebook)
    TextView textFacebook;
    @BindView(R.id.rating_bar)
    RatingBar ratingBar;
    @BindView(R.id.txt_rating_score)
    TextView ratingScore;
    @BindView(R.id.txt_description)
    TextView textDescription;
    @BindView(R.id.btn_website)
    ImageButton websiteRedirectButton;
    @BindView(R.id.btn_facebook)
    ImageButton facebookRedirectButton;
    @BindView(R.id.fab_edit)
    FloatingActionButton fabEdit;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private Shop shop;
    private boolean isGuest;
    private CompositeDisposable compositeDisposable;
    private GetOwnerShop getOwnerShop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initServices();
        Bundle bundle = getArguments();
        shop = (Shop) bundle.getSerializable(Constant.SHOP);
        isGuest = bundle.getBoolean(Constant.IS_GUEST, false);
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        getOwnerShop = Network.getInstance().createService(GetOwnerShop.class);
    }

    private void fetchUserShop() {
        compositeDisposable.add(getOwnerShop.getOwnerShop(UserAuth.getAuthUser().getAccessToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchUserShopSuccess, this::onFetchUserShopFailed));
    }

    private void onFetchUserShopSuccess(Shop shop) {
        refreshLayout.setRefreshing(false);
        this.shop = shop;
        showViews(shop);
    }

    private void onFetchUserShopFailed(Throwable e) {
        Log.i(TAG, "onFetchUserShopFailed: " + e);
        refreshLayout.setRefreshing(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop_information, container, false);
        ButterKnife.bind(this, rootView);
        initSwipeRefreshLayout();
        showViews(shop);
        return rootView;
    }

    private void initSwipeRefreshLayout() {
        int firstColor = getResources().getColor(R.color.blue);
        int secondColor = getResources().getColor(R.color.red);
        int thirdColor = getResources().getColor(R.color.yellow);
        int fourthColor = getResources().getColor(R.color.green);
        refreshLayout.setColorSchemeColors(firstColor, secondColor, thirdColor, fourthColor);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        fetchUserShop();
    }

    private void showViews(Shop shop) {
        textName.setText(shop.getName());
        textAddress.setText(shop.getAddress());
        textPhone.setText(shop.getPhone());
        String email = shop.getEmail();
        if (email == null) {
            emailGroup.setVisibility(View.GONE);
        } else {
            emailGroup.setVisibility(View.VISIBLE);
            textEmail.setText(email);
        }

        String website = shop.getWebsite();
        if (website == null) {
            websiteGroup.setVisibility(View.GONE);
        } else {
            websiteGroup.setVisibility(View.VISIBLE);
            textWebsite.setText(website);
            websiteRedirectButton.setOnClickListener(this);
        }

        String facebook = shop.getFacebookSite();
        if (facebook == null) {
            facebookGroup.setVisibility(View.GONE);
        } else {
            facebookGroup.setVisibility(View.VISIBLE);
            textFacebook.setText(facebook);
            facebookRedirectButton.setOnClickListener(this);
        }

        int rating = shop.getRating();
        ratingBar.setStar(rating);
        ratingScore.setText("" + (rating * 1.0));

        String description = shop.getDescription();
        if (description != null) {
            textDescription.setText(description);
        }

        if(isGuest) {
            fabEdit.setVisibility(View.INVISIBLE);
        } else {
            fabEdit.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_website: {
                String websiteUrl = shop.getWebsite();
                if (!websiteUrl.startsWith("http://") && !websiteUrl.startsWith("https://")) {
                    websiteUrl = "http://" + websiteUrl;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(websiteUrl));
                startActivity(intent);
            }
            break;

            case R.id.btn_facebook: {
                String facebookPageUrl = shop.getFacebookSite();
                if (!facebookPageUrl.startsWith("http://") && !facebookPageUrl.startsWith("https://")) {
                    facebookPageUrl = "http://" + facebookPageUrl;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(facebookPageUrl));
                startActivity(intent);
            }
            break;

            case R.id.fab_edit: {
                Intent intent = new Intent(getActivity(), ShopEditActivity.class);
                intent.putExtra(Constant.SHOP, shop);
                startActivityForResult(intent, SHOP_EDIT_REQUEST_CODE);
            }
            break;

            default: {
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SHOP_EDIT_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    shop = (Shop) data.getSerializableExtra(Constant.SHOP);
                    showViews(shop);
                    ((ShopPagerFragment) getParentFragment()).onShopUpdated(shop);
                }
            }
            break;

            default: {
                break;
            }
        }
    }
}
