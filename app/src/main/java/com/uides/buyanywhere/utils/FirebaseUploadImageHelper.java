package com.uides.buyanywhere.utils;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by TranThanhTung on 25/11/2017.
 */

public class FirebaseUploadImageHelper {

    public static StorageTask<UploadTask.TaskSnapshot> uploadImageToStorage(String userID, String folder, File imageFile,
                                                                              OnProgressListener<UploadTask.TaskSnapshot> onProgressListener,
                                                                              OnSuccessListener<UploadTask.TaskSnapshot> onSuccess,
                                                                              OnFailureListener onFailed) {
        Uri fileUri = Uri.fromFile(imageFile);
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(folder + "/" + userID);
        StorageTask<UploadTask.TaskSnapshot> storageTask = imageRef.putFile(fileUri)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailed);
        if (onProgressListener != null) {
            storageTask.addOnProgressListener(onProgressListener);
        }
        return storageTask;
    }
}
