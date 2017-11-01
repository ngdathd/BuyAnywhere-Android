package com.uides.buyanywhere.network.service;

import com.uides.buyanywhere.model.UserInfo;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by TranThanhTung on 18/09/2017.
 */

public interface LogInService {
    @POST("api/Auth/OAuth2/Facebook/Callback")
    Observable<UserInfo> facebookSignIn(@Body String accessToken);
}
