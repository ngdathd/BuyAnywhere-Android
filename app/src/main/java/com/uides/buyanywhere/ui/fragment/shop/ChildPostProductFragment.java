package com.uides.buyanywhere.ui.fragment.shop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.model.Image;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.ClearableEditText;
import com.uides.buyanywhere.custom_view.dialog.TwoModeProgressDialog;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.model.PostProduct;
import com.uides.buyanywhere.model.ProductPreview;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.user.PostProductService;
import com.uides.buyanywhere.ui.activity.PostProductActivity;
import com.uides.buyanywhere.utils.FirebaseUploadImageHelper;
import com.uides.buyanywhere.utils.ImagePickerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public class ChildPostProductFragment extends Fragment {
    private static final String TAG = "CPProductFragment";
    private static final int IMAGE_PICKER_REQUEST_CODE = 1;
    private static final int MAX_IMAGE_SELECTED = 6;

    @BindView(R.id.txt_input_product_name)
    ClearableEditText textName;
    @BindView(R.id.spinner_categories)
    AppCompatSpinner spinnerCategories;
    @BindView(R.id.txt_input_quantity)
    ClearableEditText textQuantity;
    @BindView(R.id.txt_input_origin_price)
    ClearableEditText textOriginPrice;
    @BindView(R.id.txt_input_off_price)
    ClearableEditText textOffPrice;
    @BindView(R.id.recycler_view_thumbnail_image)
    RecyclerView recyclerViewThumbnailImages;
    @BindView(R.id.txt_input_description)
    ClearableEditText textDescription;

    private CompositeDisposable compositeDisposable;
    private PostProductService postProductService;

    private CategoriesAdapter categoriesAdapter;
    private ImageAdapter imageAdapter;

    private FirebaseUploadImageHelper firebaseUploadImageHelper;
    private TwoModeProgressDialog twoModeProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initServices();
        imageAdapter = new ImageAdapter(getActivity());
        Bundle bundle = getArguments();
        List<Category> categories = bundle.getParcelableArrayList(Constant.CATEGORIES);
        categoriesAdapter = new CategoriesAdapter(getActivity(), categories);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_product, container, false);
        ButterKnife.bind(this, rootView);
        initViews();
        showViews();
        return rootView;
    }

    private void initViews() {
        textOffPrice.setText("0");
        textQuantity.setText("1");
        twoModeProgressDialog = new TwoModeProgressDialog(getActivity());
        firebaseUploadImageHelper = new FirebaseUploadImageHelper();

        firebaseUploadImageHelper.setOnProgressListener(twoModeProgressDialog);
        firebaseUploadImageHelper.setOnNextTaskListener(twoModeProgressDialog);

        firebaseUploadImageHelper.setOnFailureListener((index, total, e) -> {
            Log.i(TAG, "uploadImage: " + index + "/" + total + ": " + e);
            twoModeProgressDialog.dismiss();
            Toast.makeText(getActivity(), R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
        firebaseUploadImageHelper.cancel();
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        postProductService = Network.getInstance().createService(PostProductService.class);
    }

    private void showViews() {
        spinnerCategories.setAdapter(categoriesAdapter);

        recyclerViewThumbnailImages.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerViewThumbnailImages.setAdapter(imageAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit) {
            if (!textName.validate() ||
                    !textQuantity.validate()) {
                return false;
            }

            int quantity = Integer.parseInt(textQuantity.getText());
            if (quantity < 1) {
                textQuantity.setError(getString(R.string.quantity_positive));
                return false;
            }

            if (!textOriginPrice.validate()) {
                return false;
            }

            long originPrice = Long.parseLong(textOriginPrice.getText());
            if (originPrice < 1000) {
                textOriginPrice.setError(getString(R.string.price_is_equal_or_more_than_1000));
                return false;
            }

            String offPriceText = textOffPrice.getText();
            long offPrice = 0;
            if (!offPriceText.isEmpty()) {
                offPrice = Long.parseLong(textOffPrice.getText());
            }

            if (offPrice != 0 && offPrice < 1000) {
                textOffPrice.setError(getString(R.string.price_is_equal_or_more_than_1000));
                return false;
            }

            if (originPrice <= offPrice) {
                textOriginPrice.setError(getString(R.string.invalid_price));
                return false;
            }

            PostProduct product = new PostProduct();
            product.setName(textName.getText());
            product.setShopId(UserAuth.getAuthUser().getShopID());
            product.setOriginPrice(originPrice);
            product.setCurrentPrice(offPrice == 0 ? originPrice : offPrice);
            product.setCategoryId(((Category) spinnerCategories.getSelectedItem()).getId());
            product.setDescription(textDescription.getText());
            product.setQuantity(Integer.parseInt(textQuantity.getText()));

            uploadImage(product);
        }
        return super.onOptionsItemSelected(item);
    }


    private void uploadImage(PostProduct product) {
        long date = new Date().getTime();
        String imageDir = "products/" + UserAuth.getAuthUser().getShopID() + "/" + date + "/";

        int itemCount = imageAdapter.getItemCount();
        int imageCount = itemCount == MAX_IMAGE_SELECTED? itemCount : itemCount - 1;

        List<Image> images = new ArrayList<>(imageAdapter.getItemCount() - 1);
        for (int i = 0; i < imageCount; i++) {
            images.add(imageAdapter.getItem(i, Image.class));
        }

        firebaseUploadImageHelper.setOnCompleteListener(urls -> {
            if (!urls.isEmpty()) {
                product.setPreviewUrl(urls.get(0));
            }
            product.setDescriptiveImageUrl(urls);

            postProduct(product);
        });

        firebaseUploadImageHelper.uploadImagesToStorage(imageDir, images);
        twoModeProgressDialog.show();
    }

    private void postProduct(PostProduct product) {
        twoModeProgressDialog.switchToIndeterminateMode();
        compositeDisposable.add(postProductService
                .postProduct(UserAuth.getAuthUser().getAccessToken(), product)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPostProductSuccess, this::onPostProductFailed));
    }

    private void onPostProductSuccess(ProductPreview productPreview) {
        twoModeProgressDialog.dismiss();
        ((PostProductActivity) getActivity()).returnPostProductAndFinish(productPreview);
    }

    private void onPostProductFailed(Throwable e) {
        Log.i(TAG, "onPostProductFailed: " + e);
        twoModeProgressDialog.dismiss();
        Toast.makeText(getActivity(), R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    private class CategoriesAdapter extends BaseAdapter {
        List<Category> categories;
        LayoutInflater layoutInflater;

        public CategoriesAdapter(Context context, List<Category> categories) {
            this.categories = categories;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Category getItem(int position) {
            return categories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CategoryViewHolder categoryViewHolder;
            if (convertView == null) {
                TextView textView = (TextView) layoutInflater.inflate(R.layout.spinner_item, parent, false);
                convertView = textView;
                categoryViewHolder = new CategoryViewHolder();
                categoryViewHolder.textName = textView;
                convertView.setTag(categoryViewHolder);
            } else {
                categoryViewHolder = (CategoryViewHolder) convertView.getTag();
            }
            categoryViewHolder.textName.setText(getItem(position).getName());
            return convertView;
        }
    }

    private class CategoryViewHolder {
        private TextView textName;
    }

    private class ImageAdapter extends RecyclerViewAdapter {
        public static final int ADD_IMAGE_VIEW_TYPE = 2;

        public ImageAdapter(Context context) {
            super(context, false);
            addModel(null, ADD_IMAGE_VIEW_TYPE, false);
        }

        @Override
        protected RecyclerView.ViewHolder solvedOnCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ADD_IMAGE_VIEW_TYPE: {
                    View view = getInflater().inflate(R.layout.item_image_add, parent, false);
                    return new AddImageViewHolder(view);
                }

                default: {
                    return initNormalViewHolder(parent);
                }
            }
        }

        @Override
        protected void solvedOnBindViewHolder(RecyclerView.ViewHolder viewHolder, int viewType, int position) {
            switch (viewType) {
                case VIEW_TYPE_NORMAL: {
                    bindNormalViewHolder((NormalViewHolder) viewHolder, position);
                }
                break;

                default: {
                    break;
                }
            }
        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View view = getInflater().inflate(R.layout.item_image_thumbnail, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            Image image = getItem(position, Image.class);
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;

            Picasso.with(getActivity())
                    .load(new File(image.getPath()))
                    .fit()
                    .into(imageViewHolder.imageThumbnail);
        }

        @Override
        public void removeModel(int index) {
            super.removeModel(index);
            int itemCount = getItemCount();
            if (itemCount < MAX_IMAGE_SELECTED) {
                if (getItem(itemCount - 1, Object.class) != null) {
                    addModel(null, ADD_IMAGE_VIEW_TYPE, false);
                }
            }
        }

        @Override
        public <T> void addModels(List<T> listModels, boolean isScroll) {
            int itemCount = getItemCount();
            int lastIndex = itemCount - 1;
            int size = listModels.size();
            for (int i = 0; i < size; i++) {
                addModel(lastIndex, listModels.get(i), VIEW_TYPE_NORMAL, false, false);
                lastIndex++;
            }
            if (itemCount + size >= MAX_IMAGE_SELECTED) {
                removeModel(lastIndex);
            }
            notifyDataSetChanged();
            if (isScroll) {
                getRecyclerView().scrollToPosition(getItemCount() - 1);
            }
        }
    }

    public class AddImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AddImageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int imageLimit = MAX_IMAGE_SELECTED - imageAdapter.getItemCount() + 1;
            if (imageLimit <= 0) {
                return;
            }
            ImagePickerHelper.startImagePickerActivity(ChildPostProductFragment.this, IMAGE_PICKER_REQUEST_CODE, imageLimit);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMAGE_PICKER_REQUEST_CODE: {
                List<Image> selectedImages = ImagePickerHelper.getSelectedImage(resultCode, data);
                imageAdapter.addModels(selectedImages, false);
            }
            break;

            default: {
                break;
            }
        }
    }

    public class ImageViewHolder extends RecyclerViewAdapter.NormalViewHolder implements View.OnClickListener {
        @BindView(R.id.img_thumbnail)
        ImageView imageThumbnail;
        @BindView(R.id.btn_delete)
        ImageButton buttonDelete;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            buttonDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            imageAdapter.removeModel(getAdapterPosition());
        }
    }
}
