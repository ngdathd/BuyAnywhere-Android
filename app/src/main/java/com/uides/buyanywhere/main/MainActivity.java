package com.uides.buyanywhere.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.model.UserInfo;
import com.uides.buyanywhere.network.retrofit.Network;
import com.uides.buyanywhere.network.service.CategoriesService;
import com.uides.buyanywhere.network.service.EnableNotificationService;
import com.uides.buyanywhere.utils.ImagePickerBuilder;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 15/09/2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int IMAGE_PICKER_REQUEST_CODE = 0;

    @BindView(R.id.txt_greeting)
    TextView textGreeting;
    @BindView(R.id.img_image)
    ImageView imageSelected;
    @BindView(R.id.btn_select_image)
    Button buttonSelectImage;
    @BindView(R.id.btn_upload)
    Button buttonUpload;
    @BindView(R.id.txt_image_url)
    TextView textImageUrl;
    @BindView(R.id.btn_send_notification)
    Button buttonSendNotification;

    private StorageReference firebaseStorageRef;
    private List<Image> listImageSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra(Constant.USER_INFO);
        if (userInfo != null) {
            textGreeting.setText("Hello, " + userInfo.getName());
        }

        firebaseStorageRef = FirebaseStorage.getInstance().getReference();

        buttonSelectImage.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        buttonSendNotification.setOnClickListener(this);
    }

    private class Adapter extends BaseAdapter {
        LayoutInflater layoutInflater;
        List<Category> list;

        public Adapter(Context context, List<Category> list) {
            layoutInflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rootView = layoutInflater.inflate(R.layout.item, viewGroup, false);
            Category category = list.get(i);
            ((TextView) rootView.findViewById(R.id.txt_name)).setText(category.getName());
            return rootView;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_image: {
                new ImagePickerBuilder(this)
                        .withSingleSelectionMode()
                        .build()
                        .start(IMAGE_PICKER_REQUEST_CODE);
            }
            break;

            case R.id.btn_upload: {
                if (listImageSelected != null && listImageSelected.size() > 0) {
                    File image = new File(listImageSelected.get(0).getPath());
                    Uri selectedImageUri = Uri.fromFile(image);
                    StorageReference imageRef = firebaseStorageRef.child("images/" + image.getName());
                    imageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri imageUrl = taskSnapshot.getDownloadUrl();
                                    if (imageUrl != null)
                                        textImageUrl.setText(imageUrl.getPath());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    textImageUrl.setText(R.string.upload_failed);
                                }
                            });
                } else {
                    Toast.makeText(this,"Select image first", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case R.id.btn_send_notification: {
                String token = FirebaseInstanceId.getInstance().getToken();
                EnableNotificationService notificationService = Network.getInstance().createService(EnableNotificationService.class);
                notificationService.enableNotification(token, true)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Object>() {

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(MainActivity.this, "Send Notification failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(Object obj) {
                                Toast.makeText(MainActivity.this, "Send Notification successed", Toast.LENGTH_SHORT).show();
                            }
                        });
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
            case IMAGE_PICKER_REQUEST_CODE: {
                if (resultCode == RESULT_OK && data != null) {
                    listImageSelected = ImagePicker.getImages(data);
                    imageSelected.setImageBitmap(BitmapFactory.decodeFile(listImageSelected.get(0).getPath()));
                }
            }
            break;

            default: {
                break;
            }
        }
    }
}
