package com.uides.buyanywhere.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esafirm.imagepicker.model.Image;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.ClearableEditText;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.model.google_map.Location;
import com.uides.buyanywhere.model.Shop;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.user.UpdateOwnerShopService;
import com.uides.buyanywhere.utils.FirebaseUploadImageHelper;
import com.uides.buyanywhere.utils.ImagePickerHelper;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 24/11/2017.
 */

public class ShopEditActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ShopEditActivity";
    private static final int COVER_IMAGE_PICKER_REQUEST_CODE = 0;
    private static final int AVATAR_IMAGE_PICKER_REQUEST_CODE = 1;
    private static final int LOCATION_REQUEST_CODE = 2;

    @BindView(R.id.txt_input_name)
    ClearableEditText editShopName;
    @BindView(R.id.txt_input_address)
    TextInputLayout textInputAddress;
    @BindView(R.id.edt_address)
    EditText editAddress;
    @BindView(R.id.txt_input_phone)
    ClearableEditText editPhone;
    @BindView(R.id.txt_input_email)
    ClearableEditText editEmail;
    @BindView(R.id.txt_input_website)
    ClearableEditText editWebsite;
    @BindView(R.id.txt_input_facebook)
    ClearableEditText editFacebook;
    @BindView(R.id.txt_input_description)
    ClearableEditText editDescription;
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

    private LoadingDialog loadingDialog;

    private CompositeDisposable compositeDisposable;

    private Shop shop;
    private Shop tempShop;

    private FirebaseUploadImageHelper avatarUploadHelper;
    private FirebaseUploadImageHelper coverUploadHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        shop = (Shop) intent.getSerializableExtra(Constant.SHOP);
        initTempShop(shop);
        initServices();
        initViews();
        initToolBar();
        showViews(shop);
    }

    private void initTempShop(Shop shop) {
        tempShop = new Shop();
        tempShop.setId(shop.getId());
        tempShop.setOwnerId(shop.getOwnerId());
        tempShop.setName(shop.getName());
        tempShop.setAddress(shop.getAddress());
        tempShop.setPhone(shop.getPhone());
        tempShop.setEmail(shop.getEmail());
        tempShop.setWebsite(shop.getWebsite());
        tempShop.setFacebookSite(shop.getFacebookSite());
        tempShop.setDescription(shop.getDescription());
        tempShop.setRating(shop.getRating());
        tempShop.setCreatedDate(shop.getCreatedDate());
        tempShop.setAvatar(shop.getAvatar());
        tempShop.setCover(shop.getCover());
        tempShop.setLat(shop.getLat());
        tempShop.setLon(shop.getLon());
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();

        avatarUploadHelper = new FirebaseUploadImageHelper();
        avatarUploadHelper.setOnSuccessListener((index, total, taskSnapshot) -> {
            tempShop.setAvatar(taskSnapshot.getDownloadUrl().toString());
            Toast.makeText(ShopEditActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();

            buttonReUploadAvatar.setTag(null);
            buttonCameraAvatar.setVisibility(View.VISIBLE);
            showProgress(progressAvatar, false);
        });

        coverUploadHelper = new FirebaseUploadImageHelper();
        coverUploadHelper.setOnSuccessListener((index, total, taskSnapshot) -> {
            tempShop.setCover(taskSnapshot.getDownloadUrl().toString());
            Toast.makeText(ShopEditActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();

            buttonReUploadCover.setTag(null);
            buttonCameraCover.setVisibility(View.VISIBLE);
            showProgress(progressCover, false);
        });
    }

    private void initViews() {
        buttonCameraCover.setOnClickListener(this);
        buttonCameraAvatar.setOnClickListener(this);
        loadingDialog = new LoadingDialog(this);
        editAddress.setOnClickListener(this);
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.edit_shop);
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

    private void showViews(Shop shop) {
        Picasso.with(this).load(shop.getCover())
                .placeholder(R.drawable.shop_cover_place_holder)
                .fit()
                .into(imageCover);
        Picasso.with(this).load(shop.getAvatar())
                .placeholder(R.drawable.shop_avatar_placeholder)
                .into(imageAvatar);

        imageCover.setOnClickListener(this);
        imageAvatar.setOnClickListener(this);

        editShopName.setText(shop.getName());

        editAddress.setText(shop.getAddress());

        editPhone.setText(shop.getPhone());

        String email = shop.getEmail();
        if (email != null) {
            editEmail.setText(email);
        }

        String website = shop.getWebsite();
        if (website != null) {
            editWebsite.setText(website);
        }

        String facebook = shop.getFacebookSite();
        if (facebook != null) {
            editFacebook.setText(facebook);
        }

        String description = shop.getDescription();
        if (description != null) {
            editDescription.setText(description);
        }
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

                if (!editShopName.validate() ||
                        !editPhone.validate() ||
                        !editEmail.validate() ||
                        !editWebsite.validate() ||
                        !editFacebook.validate() ||
                        !editDescription.validate()) {
                    return false;
                }

                String address = editAddress.getText().toString();
                if (address.isEmpty()) {
                    textInputAddress.setError(getString(R.string.pick_address_required));
                    return false;
                }

//                tempShop.setName(editShopName.getText());//not able to update name yet :v
                tempShop.setAddress(address);
                tempShop.setPhone(editPhone.getText());
                tempShop.setEmail(editEmail.getText());
                tempShop.setWebsite(editWebsite.getText());
                tempShop.setFacebookSite(editFacebook.getText());
                tempShop.setDescription(editDescription.getText());

                updateShop(tempShop);
            }
            break;

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateShop(Shop shop) {
        loadingDialog.show();
        compositeDisposable = new CompositeDisposable();
        UpdateOwnerShopService updateOwnerShopService = Network.getInstance().createService(UpdateOwnerShopService.class);
        compositeDisposable.add(updateOwnerShopService.updateOwnerShop(UserAuth.getAuthUser().getAccessToken(), shop)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                            onUpdateSuccess(shop);
                        },
                        this::onUpdateFailed));
    }

    void onUpdateSuccess(Shop newShop) {
        loadingDialog.hide();
        shop.setCover(newShop.getCover());
        shop.setAvatar(newShop.getAvatar());
        shop.setName(newShop.getName());
        shop.setAddress(newShop.getAddress());
        shop.setPhone(newShop.getPhone());
        shop.setEmail(newShop.getEmail());
        shop.setWebsite(newShop.getWebsite());
        shop.setFacebookSite(newShop.getFacebookSite());
        shop.setDescription(newShop.getDescription());

        Intent intent = new Intent();
        intent.putExtra(Constant.SHOP, shop);
        setResult(RESULT_OK, intent);
        finish();
    }

    void onUpdateFailed(Throwable throwable) {
        loadingDialog.hide();
        Log.i(TAG, "onUpdateFailed: " + throwable);
        Toast.makeText(this, R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

            case R.id.edt_address: {
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra(Constant.ADDRESS, tempShop.getAddress());
                intent.putExtra(Constant.LOCATION, new Location(tempShop.getLat(), tempShop.getLon()));
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
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

            case LOCATION_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    String address = data.getStringExtra(Constant.ADDRESS);
                    tempShop.setAddress(address);
                    editAddress.setText(address);

                    Location location = (Location) data.getSerializableExtra(Constant.LOCATION);
                    tempShop.setLat(location.getLat());
                    tempShop.setLon(location.getLng());
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
            Log.i(TAG, "onUploadAvatarFailed: " + e);
            Toast.makeText(ShopEditActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();

            buttonCameraAvatar.setVisibility(View.VISIBLE);
            buttonReUploadAvatar.setVisibility(View.VISIBLE);
            buttonReUploadAvatar.setTag(imageFile);
            showProgress(progressAvatar, false);
        });

        avatarUploadHelper.uploadImageToStorage(UserAuth.getAuthUser().getId(), Constant.SHOP_AVATARS, imageFile);
    }

    private void uploadCoverImage(File imageFile) {
        showProgress(progressCover, true);
        buttonCameraCover.setVisibility(View.INVISIBLE);
        buttonReUploadCover.setVisibility(View.INVISIBLE);
        buttonReUploadCover.setTag(null);

        coverUploadHelper.setOnFailureListener((index, total, e) -> {
            Log.i(TAG, "onUploadAvatarFailed: " + e);
            Toast.makeText(ShopEditActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();

            buttonCameraCover.setVisibility(View.VISIBLE);
            buttonReUploadCover.setVisibility(View.VISIBLE);
            buttonReUploadCover.setTag(imageFile);
            showProgress(progressCover, false);
        });

        coverUploadHelper.uploadImageToStorage(UserAuth.getAuthUser().getId(), Constant.SHOP_COVERS, imageFile);
    }

    public void showProgress(ProgressBar progressBar, boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
        }
    }
}
