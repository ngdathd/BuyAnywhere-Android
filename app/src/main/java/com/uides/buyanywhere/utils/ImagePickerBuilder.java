package com.uides.buyanywhere.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.esafirm.imagepicker.features.ImagePicker;
import com.uides.buyanywhere.Constant;

/**
 * Created by TranThanhTung on 25/09/2017.
 */

public class ImagePickerBuilder {
    private ImagePicker imagePicker;

    public ImagePickerBuilder(Activity activity) {
        this.imagePicker = ImagePicker.create(activity);
        withTitle(Constant.IMAGE_PICKER_DEFAULT_TITLE);
        imagePicker.showCamera(true);
    }

    public ImagePickerBuilder(Fragment fragment) {
        this.imagePicker = ImagePicker.create(fragment);
        withTitle(Constant.IMAGE_PICKER_DEFAULT_TITLE);
        imagePicker.showCamera(true);
    }

    public ImagePickerBuilder withTitle(String title) {
        imagePicker.imageTitle(title);
        return this;
    }

    public ImagePickerBuilder withSingleSelectionMode() {
        imagePicker.single();
        return this;
    }

    public ImagePickerBuilder withMultipleSelectionMode(int limit) {
        imagePicker.multi();
        if(limit > 0 || limit <= 99) {
            imagePicker.limit(limit);
        }
        imagePicker.returnAfterFirst(false);
        return this;
    }

    public ImagePicker build() {
        return imagePicker;
    }
}
