package com.uides.buyanywhere.ui.fragment.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.model.UserProfile;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.user.GetFavoriteCategoriesService;
import com.uides.buyanywhere.service.user.GetUserProfileService;
import com.uides.buyanywhere.utils.GenderUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 18/12/2017.
 */

public class GuestProfileChildFragment extends Fragment {
    private static final String TAG = "GPChildFragment";
    @BindView(R.id.img_cover)
    ImageView imageCover;
    @BindView(R.id.img_avatar)
    CircleImageView imageAvatar;
    @BindView(R.id.txt_address_title)
    TextView textAddressTitle;
    @BindView(R.id.txt_name)
    TextView textName;
    @BindView(R.id.txt_email)
    TextView textEmail;
    @BindView(R.id.txt_address)
    TextView textAddress;
    @BindView(R.id.txt_phone)
    TextView textPhone;
    @BindView(R.id.txt_gender)
    TextView textGender;
    @BindView(R.id.tag_group_favorite)
    TagView favoriteTagGroup;

    private View rootView;

    private CompositeDisposable compositeDisposable;
    private GetUserProfileService getUserProfileService;
    private GetFavoriteCategoriesService getFavoriteCategoriesService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_guest_profile, container, false);
        ButterKnife.bind(this, rootView);

        UserProfile userProfile = (UserProfile) getArguments().getSerializable(Constant.USER_PROFILE);

        setHasOptionsMenu(true);

        initToolBar();
        initServices();
        showViews(userProfile);

        return rootView;
    }

    private void initToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        Network network = Network.getInstance();
        getUserProfileService = network.createService(GetUserProfileService.class);
        getFavoriteCategoriesService = Network.getInstance().createService(GetFavoriteCategoriesService.class);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void showViews(UserProfile userProfile) {
        Context context = getActivity();
        Picasso.with(context).load(userProfile.getCoverUrl())
                .placeholder(R.drawable.profile_cover)
                .fit()
                .into(imageCover);
        Picasso.with(context).load(userProfile.getAvatarUrl())
                .placeholder(R.drawable.avatar_placeholder)
                .fit()
                .centerCrop()
                .into(imageAvatar);
        textName.setText(userProfile.getName());

        String email = userProfile.getEmail();
        if (email == null) {
            email = "";
        }
        textEmail.setText(email);

        String userAddress = userProfile.getAddress();
        if (userAddress == null) {
            userAddress = "";
        }
        textAddress.setText(userAddress);
        textAddressTitle.setText(userAddress);

        String userPhone = userProfile.getPhone();
        if (userPhone == null) {
            userPhone = "";
        }
        textPhone.setText(userPhone);

        String gender = userProfile.getGender();
        textGender.setText(GenderUtils.getGenderVi(gender));

        favoriteTagGroup.removeAll();
        List<Category> favoriteCategories = userProfile.getFavoriteCategories();
        if (favoriteCategories != null) {
            for (Category category : favoriteCategories) {
                Tag tag = new Tag(category.getName());
                tag.tagTextSize = 12;
                favoriteTagGroup.addTag(tag);
            }
        }
    }

}
