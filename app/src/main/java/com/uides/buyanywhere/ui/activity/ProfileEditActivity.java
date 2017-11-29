package com.uides.buyanywhere.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.ClearableEditText;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.model.UserProfile;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.categories.GetCategoriesService;
import com.uides.buyanywhere.service.user.AddFavoriteCategoriesService;
import com.uides.buyanywhere.service.user.DeleteFavoriteCategoriesService;
import com.uides.buyanywhere.service.user.UpdateUserProfileService;
import com.uides.buyanywhere.utils.FirebaseUploadImageHelper;
import com.uides.buyanywhere.utils.ImagePickerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 24/11/2017.
 */

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileEditActivity";
    private static final int COVER_IMAGE_PICKER_REQUEST_CODE = 0;
    private static final int AVATAR_IMAGE_PICKER_REQUEST_CODE = 1;

    @BindView(R.id.txt_input_name)
    ClearableEditText editName;
    @BindView(R.id.txt_input_address)
    ClearableEditText editAddress;
    @BindView(R.id.txt_input_phone)
    ClearableEditText editPhone;
    @BindView(R.id.tag_group_favorite)
    TagView favoriteTagGroup;
    @BindView(R.id.group_gender)
    RadioGroup genderRadioGroup;
    @BindView(R.id.btn_add)
    ImageButton addButton;
    @BindView(R.id.progress_add)
    ProgressBar progressAdd;
    @BindView(R.id.progress_cover)
    ProgressBar progressCover;
    @BindView(R.id.progress_avatar)
    ProgressBar progressAvatar;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.img_cover)
    ImageView imageCover;
    @BindView(R.id.img_avatar)
    ImageView imageAvatar;
    @BindView(R.id.btn_reupload_cover)
    ImageButton buttonReUploadCover;
    @BindView(R.id.btn_reupload_avatar)
    ImageButton buttonReUploadAvatar;
    @BindView(R.id.btn_camera_avatar)
    ImageButton buttonCameraAvatar;
    @BindView(R.id.btn_camera_cover)
    ImageButton buttonCameraCover;

    private List<Category> categories;
    private CompositeDisposable compositeDisposable;

    private Map<String, Category> addedCategories;
    private Map<String, Category> deletedCategories;
    private Map<String, Category> userFavoriteCategories;
    private Map<String, Category> currentFavoriteCategories;

    private UserProfile userProfile;

    private FirebaseUploadImageHelper avatarUploadHelper;
    private FirebaseUploadImageHelper coverUploadHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        userProfile = (UserProfile) intent.getSerializableExtra(Constant.USER_PROFILE);
        initServices();
        initViews();
        initToolBar();
        showViews(userProfile);
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();

        avatarUploadHelper = new FirebaseUploadImageHelper();
        avatarUploadHelper.setOnSuccessListener((index, total, taskSnapshot) -> {
            userProfile.setAvatarUrl(taskSnapshot.getDownloadUrl().toString());
            Toast.makeText(ProfileEditActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();

            buttonReUploadAvatar.setTag(null);
            showProgress(progressAvatar, false);
            buttonCameraAvatar.setVisibility(View.VISIBLE);
        });

        coverUploadHelper = new FirebaseUploadImageHelper();
        coverUploadHelper.setOnSuccessListener((index, total, taskSnapshot) -> {
            userProfile.setCoverUrl(taskSnapshot.getDownloadUrl().toString());
            Toast.makeText(ProfileEditActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();

            buttonReUploadCover.setTag(null);
            showProgress(progressCover, false);
            buttonCameraCover.setVisibility(View.VISIBLE);
        });
    }

    private void initViews() {
        buttonCameraAvatar.setOnClickListener(this);
        buttonCameraCover.setOnClickListener(this);
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.edit_profile);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        avatarUploadHelper.cancel();
        coverUploadHelper.cancel();
    }

    private void showViews(UserProfile userProfile) {
        Picasso.with(this).load(userProfile.getCoverUrl()).into(imageCover);
        Picasso.with(this).load(userProfile.getAvatarUrl()).into(imageAvatar);

        imageCover.setOnClickListener(this);
        imageAvatar.setOnClickListener(this);

        addButton.setOnClickListener(this);

        editName.setText(userProfile.getName());

        String address = userProfile.getAddress();
        if (address != null) {
            editAddress.setText(address);
        }

        String phone = userProfile.getPhone();
        if (phone != null) {
            editPhone.setText(phone);
        }

        userFavoriteCategories = new HashMap<>();
        currentFavoriteCategories = new HashMap<>();
        addedCategories = new HashMap<>();
        deletedCategories = new HashMap<>();

        List<Category> favoriteCategories = userProfile.getFavoriteCategories();
        if (favoriteCategories != null) {
            for (Category category : favoriteCategories) {
                userFavoriteCategories.put(category.getId(), category);
                currentFavoriteCategories.put(category.getId(), category);
                addFavoriteCategoryTag(category.getId(), category.getName());
            }
            favoriteTagGroup.setOnTagDeleteListener((tagView, tag, i) -> {
                CustomTag customTag = (CustomTag) tag;
                tagView.remove(i);
                Category deletedCategory = userFavoriteCategories.get(customTag.tagID);
                if (deletedCategory != null) {
                    deletedCategories.put(deletedCategory.getId(), deletedCategory);
                } else {
                    addedCategories.remove(customTag.tagID);
                }
                currentFavoriteCategories.remove(customTag.tagID);
            });
        }

        String gender = userProfile.getGender();
        if (gender == null) {
            genderRadioGroup.check(R.id.radio_none);
        } else {
            switch (gender) {
                case "male": {
                    genderRadioGroup.check(R.id.radio_male);
                }
                break;

                case "female": {
                    genderRadioGroup.check(R.id.radio_female);
                }
                break;

                default: {
                    break;
                }
            }
        }
    }

    public void addFavoriteCategoryTag(String id, String name) {
        CustomTag tag = new CustomTag(id, name);
        tag.tagTextSize = 12;
        tag.isDeletable = true;
        favoriteTagGroup.addTag(tag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
            break;

            case R.id.action_submit: {
                if (avatarUploadHelper.isPerformingUploadTask() || coverUploadHelper.isPerformingUploadTask()) {
                    Toast.makeText(this, R.string.upload_task_not_done, Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!editName.validate() || !editAddress.validate() || !editPhone.validate()) {
                    return false;
                }
                UserProfile userProfile = new UserProfile();
                userProfile.setName(editName.getText());
                userProfile.setAddress(editAddress.getText());
                userProfile.setPhone(editPhone.getText());
                userProfile.setCoverUrl(this.userProfile.getCoverUrl());
                userProfile.setAvatarUrl(this.userProfile.getAvatarUrl());
                switch (genderRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.radio_male: {
                        userProfile.setGender("male");
                    }
                    break;

                    case R.id.radio_female: {
                        userProfile.setGender("female");
                    }
                    break;

                    case R.id.radio_none: {
                        userProfile.setGender("");
                    }
                    break;

                    default: {
                        break;
                    }
                }

                List<Observable<Object>> observables = new ArrayList<>(3);
                Network network = Network.getInstance();
                String accessToken = UserAuth.getAuthUser().getAccessToken();

                List<String> added = new ArrayList<>();
                for (Map.Entry<String, Category> entry : addedCategories.entrySet()) {
                    added.add(entry.getKey());
                }

                List<String> deleted = new ArrayList<>();
                for (Map.Entry<String, Category> entry : deletedCategories.entrySet()) {
                    deleted.add(entry.getKey());
                }

                if (!added.isEmpty()) {
                    //add user favorite categories service
                    observables.add(network.createService(AddFavoriteCategoriesService.class)
                            .addFavoriteCategories(accessToken, added));
                }

                if (!deleted.isEmpty()) {
                    //delete user favorite categories service
                    observables.add(network.createService(DeleteFavoriteCategoriesService.class)
                            .deleteFavoriteCategories(accessToken, deleted));
                }

                //update user profile service Hoài Đức, Hà Nội
                observables.add(network.createService(UpdateUserProfileService.class)
                        .updateUserProfile(accessToken, userProfile));

                Observable.combineLatest(observables, objects -> userProfile).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(this::onUpdateSuccess, this::onUpdateFailed);
            }
            break;

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void onUpdateSuccess(UserProfile newUserProfile) {
        userProfile.setName(newUserProfile.getName());
        userProfile.setAddress(newUserProfile.getAddress());
        userProfile.setPhone(newUserProfile.getPhone());
        userProfile.setGender(newUserProfile.getGender());

        List<Category> newFavoriteCategories = new ArrayList<>();
        for (Map.Entry<String, Category> entry : currentFavoriteCategories.entrySet()) {
            newFavoriteCategories.add(entry.getValue());
        }

        userProfile.setFavoriteCategories(newFavoriteCategories);


        Intent intent = new Intent();
        intent.putExtra(Constant.USER_PROFILE, userProfile);
        setResult(RESULT_OK, intent);
        finish();
    }

    void onUpdateFailed(Throwable throwable) {
        Log.i(TAG, "onUpdateFailed: " + throwable);
        throwable.printStackTrace();
        Toast.makeText(this, R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add: {
                if (categories == null) {
                    setAddButtonLoadingState(true);
                    fetchCategories();
                } else {
                    showCategorySelectionDialog();
                }
            }
            break;

            case R.id.btn_camera_avatar: {
                ImagePickerHelper.startImagePickerActivity(this, AVATAR_IMAGE_PICKER_REQUEST_CODE);
            }
            break;

            case R.id.btn_camera_cover: {
                ImagePickerHelper.startImagePickerActivity(this, COVER_IMAGE_PICKER_REQUEST_CODE);
            }
            break;

            case R.id.btn_reupload_avatar: {
                buttonReUploadAvatar.setVisibility(View.INVISIBLE);
                uploadAvatarImage((File) buttonReUploadAvatar.getTag());
            }
            break;

            case R.id.btn_reupload_cover: {
                buttonReUploadCover.setVisibility(View.INVISIBLE);
                uploadCoverImage((File) buttonReUploadCover.getTag());
            }
            break;

            default: {
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AVATAR_IMAGE_PICKER_REQUEST_CODE: {
                List<Image> selectedImages = ImagePickerHelper.getSelectedImage(resultCode, data);
                if (!selectedImages.isEmpty()) {
                    Image image = selectedImages.get(0);
                    File imageFile = new File(image.getPath());
                    Picasso.with(this).load(imageFile).into(imageAvatar);
                    uploadAvatarImage(imageFile);
                }
            }
            break;

            case COVER_IMAGE_PICKER_REQUEST_CODE: {
                List<Image> selectedImages = ImagePickerHelper.getSelectedImage(resultCode, data);
                if (!selectedImages.isEmpty()) {
                    Image image = selectedImages.get(0);
                    File imageFile = new File(image.getPath());
                    Picasso.with(this).load(imageFile).into(imageCover);
                    uploadCoverImage(imageFile);
                }
            }
            break;

            default: {
                break;
            }
        }
    }

    private void uploadAvatarImage(File imageFile) {
        showProgress(progressAvatar, true);
        buttonCameraAvatar.setVisibility(View.INVISIBLE);
        buttonReUploadAvatar.setVisibility(View.INVISIBLE);
        buttonReUploadAvatar.setTag(null);

        avatarUploadHelper.setOnFailureListener((index, total, e) -> {
            Log.i(TAG, "uploadAvatarImage: " + e);
            Toast.makeText(ProfileEditActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();

            buttonReUploadAvatar.setVisibility(View.VISIBLE);
            buttonReUploadAvatar.setTag(imageFile);
            showProgress(progressAvatar, false);
            buttonCameraAvatar.setVisibility(View.VISIBLE);
        });

        avatarUploadHelper.uploadImageToStorage(UserAuth.getAuthUser().getId(), Constant.AVATARS, imageFile);
    }

    private void uploadCoverImage(File imageFile) {
        showProgress(progressCover, true);
        buttonCameraCover.setVisibility(View.INVISIBLE);
        buttonReUploadCover.setVisibility(View.INVISIBLE);
        buttonReUploadCover.setTag(null);

        coverUploadHelper.setOnFailureListener((index, total, e) -> {
            Log.i(TAG, "onUploadAvatarFailed: " + e);
            Toast.makeText(ProfileEditActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();

            buttonReUploadCover.setVisibility(View.VISIBLE);
            buttonReUploadCover.setTag(imageFile);
            showProgress(progressCover, false);
            buttonCameraCover.setVisibility(View.VISIBLE);
        });

        coverUploadHelper.uploadImageToStorage(UserAuth.getAuthUser().getId(), Constant.COVERS, imageFile);
    }

    public void showProgress(ProgressBar progressBar, boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
        }
    }

    public void setAddButtonLoadingState(boolean isLoading) {
        addButton.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        progressAdd.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

    private void fetchCategories() {
        Disposable disposable = Network.getInstance().createService(GetCategoriesService.class)
                .getCategories()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchCategoriesSuccess, this::onFetchCategoriesFailed);
        compositeDisposable.add(disposable);
    }

    private void onFetchCategoriesSuccess(List<Category> categories) {
        setAddButtonLoadingState(false);
        this.categories = categories;
        showCategorySelectionDialog();
    }

    private void showCategorySelectionDialog() {
        int categoryCount = categories.size();

        CharSequence[] categoryNames = new CharSequence[categoryCount];
        boolean[] selectedItems = new boolean[categoryCount];

        Map<String, Category> selectedCategories = new HashMap<>();

        for (int i = 0; i < categoryCount; i++) {
            Category category = categories.get(i);
            if (currentFavoriteCategories.containsKey(category.getId())) {
                selectedItems[i] = true;
                selectedCategories.put(category.getId(), category);
            }
            categoryNames[i] = category.getName();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.product_categories)
                .setMultiChoiceItems(categoryNames, selectedItems, (dialog, which, isChecked) -> {
                    Category category = categories.get(which);
                    if (isChecked) {
                        selectedCategories.put(category.getId(), category);
                    } else {
                        selectedCategories.remove(category.getId());
                    }
                })
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    favoriteTagGroup.removeAll();
                    for (Map.Entry<String, Category> entry : selectedCategories.entrySet()) {
                        String key = entry.getKey();
                        if (!currentFavoriteCategories.containsKey(key)) {
                            //new item
                            if (!userFavoriteCategories.containsKey(key)) {
                                //new item not added
                                addedCategories.put(key, entry.getValue());
                            } else {
                                deletedCategories.remove(key);
                            }
                        }
                        addFavoriteCategoryTag(key, entry.getValue().getName());
                    }

                    for (Map.Entry<String, Category> entry : currentFavoriteCategories.entrySet()) {
                        String key = entry.getKey();
                        if (!selectedCategories.containsKey(key)) {
                            //lost item
                            if (userFavoriteCategories.containsKey(key)) {
                                //deletable item
                                deletedCategories.put(key, entry.getValue());
                            } else {
                                addedCategories.remove(key);
                            }
                        }
                    }

                    currentFavoriteCategories = selectedCategories;
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void onFetchCategoriesFailed(Throwable e) {
        setAddButtonLoadingState(false);
        Log.i(TAG, "onFetchCategoriesFailed: " + e);
        Toast.makeText(this, R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    private class CustomTag extends Tag {
        String tagID;

        public CustomTag(String id, String text) {
            super(text);
            this.tagID = id;
        }
    }
}
