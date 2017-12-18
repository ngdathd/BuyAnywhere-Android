package com.uides.buyanywhere.ui.fragment.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.uides.buyanywhere.ui.activity.MainActivity;
import com.uides.buyanywhere.ui.activity.ProfileEditActivity;
import com.uides.buyanywhere.utils.GenderUtils;
import com.uides.buyanywhere.utils.SharedPreferencesOpenHelper;

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
 * Created by TranThanhTung on 19/11/2017.
 */

public class ProfileChildFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final int EDIT_PROFILE_REQUEST_CODE = 0;
    private static final String TAG = "ProfileChildFragment";

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
    @BindView(R.id.fab_edit)
    FloatingActionButton editFabButton;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    private CompositeDisposable compositeDisposable;
    private GetUserProfileService getUserProfileService;
    private UserProfile userProfile;
    private GetFavoriteCategoriesService getFavoriteCategoriesService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initService();
        Bundle bundle = getArguments();
        if (bundle != null) {
            userProfile = (UserProfile) bundle.getSerializable(Constant.USER_PROFILE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        initFloatingButton();
        initSwipeRefreshLayout();
        showViews(userProfile);
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
        String accessToken = UserAuth.getAuthUser().getAccessToken();

        Observable<UserProfile> observableUserProfile = getUserProfileService
                .getUserProfile(accessToken);

        Observable<List<Category>> observableFavoriteCategories = getFavoriteCategoriesService
                .getFavoriteCategories(accessToken);

        Disposable disposable = Observable.combineLatest(observableUserProfile, observableFavoriteCategories, (userProfile, categories) -> {
            userProfile.setFavoriteCategories(categories);
            return userProfile;
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchUserProfileSuccess, this::onFetchUserProfileFailed);
        compositeDisposable.add(disposable);
    }

    private void onFetchUserProfileSuccess(UserProfile userProfile) {
        refreshLayout.setRefreshing(false);
        this.userProfile = userProfile;
        showViews(userProfile);
    }

    private void onFetchUserProfileFailed(Throwable e) {
        refreshLayout.setRefreshing(false);
        Log.i(TAG, "onFetchUserProfileFailed: " + e);
        Toast.makeText(getActivity(), R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void initService() {
        compositeDisposable = new CompositeDisposable();
        //TODO init UpdateUserProfileService
        Network network = Network.getInstance();
        getUserProfileService = network.createService(GetUserProfileService.class);
        getFavoriteCategoriesService = Network.getInstance().createService(GetFavoriteCategoriesService.class);

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

    private void initFloatingButton() {
        editFabButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_edit: {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                intent.putExtra(Constant.USER_PROFILE, userProfile);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
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
            case EDIT_PROFILE_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    UserProfile userProfile = (UserProfile) data.getSerializableExtra(Constant.USER_PROFILE);
                    this.userProfile = userProfile;

                    MainActivity mainActivity = (MainActivity) getActivity();

                    String name = userProfile.getName();
                    String email = userProfile.getEmail();
                    String avatar = userProfile.getAvatarUrl();
                    String cover = userProfile.getCoverUrl();

                    SharedPreferencesOpenHelper.saveUserProfile(mainActivity, name, email, avatar, cover);

                    mainActivity.updateDrawerHeader(name, email, avatar);

                    showViews(userProfile);
                }
            }
        }
    }
}
