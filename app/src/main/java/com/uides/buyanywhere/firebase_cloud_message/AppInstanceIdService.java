package com.uides.buyanywhere.firebase_cloud_message;

import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.uides.buyanywhere.network.service.RegisterGCMTokenService;
import com.uides.buyanywhere.network.retrofit.Network;
import com.uides.buyanywhere.utils.UserAccessToken;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 26/09/2017.
 */

public class AppInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "AppInstanceIdService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        RegisterGCMTokenService registerGCMTokenService = Network.getInstance().createService(RegisterGCMTokenService.class);
        registerGCMTokenService.register(UserAccessToken.getAccessToken(this), refreshedToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(AppInstanceIdService.this, "Register CloudTokenSuccessful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
