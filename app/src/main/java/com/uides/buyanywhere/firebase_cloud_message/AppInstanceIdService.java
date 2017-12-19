package com.uides.buyanywhere.firebase_cloud_message;

import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.uides.buyanywhere.service.auth.RegisterGCMTokenService;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.utils.UserAccessToken;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 26/09/2017.
 */

public class AppInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "AppInstanceIdService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

//        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        RegisterGCMTokenService registerGCMTokenService = Network.getInstance().createService(RegisterGCMTokenService.class);
        registerGCMTokenService.register(UserAccessToken.getAccessToken(this), refreshedToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRegisterSuccess, this::onRegisterError);
    }

    private void onRegisterError(Throwable e) {
        Toast.makeText(this, "register gcm failed", Toast.LENGTH_SHORT).show();
    }

    private void onRegisterSuccess(Object o) {
        Toast.makeText(this, "register gcm success", Toast.LENGTH_SHORT).show();
    }
}
