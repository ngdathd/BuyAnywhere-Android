package com.uides.buyanywhere.utils;

import android.net.Uri;

import com.esafirm.imagepicker.model.Image;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TranThanhTung on 25/11/2017.
 */

public class FirebaseUploadImageHelper {
    private StorageTask<UploadTask.TaskSnapshot> currentTask;
    private OnProgressListener onProgressListener;
    private OnSuccessListener onSuccessListener;
    private OnFailureListener onFailureListener;
    private OnCompleteListener onCompleteListener;
    private OnNextTaskListener onNextTaskListener;

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public void setOnNextTaskListener(OnNextTaskListener onNextTaskListener) {
        this.onNextTaskListener = onNextTaskListener;
    }

    public void setOnSuccessListener(OnSuccessListener onSuccessListener) {
        this.onSuccessListener = onSuccessListener;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void setOnFailureListener(OnFailureListener onFailureListener) {
        this.onFailureListener = onFailureListener;
    }

    public void uploadImageToStorage(String id, String folder, File imageFile) {
        Uri fileUri = Uri.fromFile(imageFile);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(folder + "/" + id);
        currentTask = imageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    currentTask = null;
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl == null) {
                        if (onFailureListener != null) {
                            onFailureListener.onFailure(0, 1, new Exception("Get download url return null"));
                        }
                    } else {
                        if (onSuccessListener != null) {
                            onSuccessListener.onSuccess(0, 1, taskSnapshot);
                        }
                        if (onCompleteListener != null) {
                            List<String> urls = new ArrayList<>(1);
                            urls.add(downloadUrl.toString());
                            onCompleteListener.onComplete(urls);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    currentTask = null;
                    if (onFailureListener != null) {
                        onFailureListener.onFailure(0, 1, e);
                    }
                })
                .addOnProgressListener(taskSnapshot -> {
                    if (onProgressListener != null) {
                        onProgressListener.onProgress(taskSnapshot);
                    }
                });
    }

    public void uploadImagesToStorage(String folder, List<Image> images) {
        int size = images.size();
        if (size == 0) {
            if (onSuccessListener != null) {
                onSuccessListener.onSuccess(0, 0, null);
            }
            if (onCompleteListener != null) {
                onCompleteListener.onComplete(Collections.emptyList());
            }
            return;
        }

        List<String> urls = new ArrayList<>(size);
        uploadImagesToStorage(0, folder, images, urls);
    }

    private void uploadImagesToStorage(int index, String folder, List<Image> images, List<String> urls) {
        Image image = images.get(index);
        File imageFile = new File(image.getPath());
        Uri fileUri = Uri.fromFile(imageFile);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(folder + "/" + index);
        currentTask = imageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    currentTask = null;
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl == null) {
                        if (onFailureListener != null) {
                            onFailureListener.onFailure(index, images.size(), new Exception("Get download url return null"));
                        }
                    } else {
                        urls.add(downloadUrl.toString());

                        if (onSuccessListener != null) {
                            onSuccessListener.onSuccess(index, images.size(), taskSnapshot);
                        }

                        if (index + 1 < images.size()) {
                            int nextIndex = index + 1;
                            uploadImagesToStorage(nextIndex, folder, images, urls);
                            if (onNextTaskListener != null) {
                                onNextTaskListener.onNextTask(nextIndex, images.size());
                            }
                        } else {
                            if (onCompleteListener != null) {
                                onCompleteListener.onComplete(urls);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    currentTask = null;
                    if (onFailureListener != null) {
                        onFailureListener.onFailure(index, images.size(), e);
                    }
                })
                .addOnProgressListener(taskSnapshot -> {
                    if (onProgressListener != null) {
                        onProgressListener.onProgress(taskSnapshot);
                    }
                });
    }

    public void cancel() {
        if (currentTask != null) {
            currentTask.cancel();
        }
    }

    public boolean isPerformingUploadTask() {
        return currentTask != null;
    }

    public interface OnSuccessListener {
        void onSuccess(int index, int total, UploadTask.TaskSnapshot taskSnapshot);
    }

    public interface OnProgressListener {
        void onProgress(UploadTask.TaskSnapshot taskSnapshot);
    }

    public interface OnNextTaskListener {
        void onNextTask(int index, int total);
    }

    public interface OnFailureListener {
        void onFailure(int index, int total, Exception e);
    }

    public interface OnCompleteListener {
        void onComplete(List<String> urls);
    }
}
