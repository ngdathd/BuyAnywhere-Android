package com.uides.buyanywhere.network.service;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.UserInfo;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import rx.Observable;

/**
 * Created by TranThanhTung on 02/10/2017.
 */

public interface EnableNotificationService {
    @PUT("api/Auth/Users/current/Notification")
    Observable<UserInfo> enableNotification(@Header(Constant.ACCESS_TOKEN) String token,
                                            @Body boolean enable);
}
