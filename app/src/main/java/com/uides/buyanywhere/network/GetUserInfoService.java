package com.uides.buyanywhere.network;

import com.uides.buyanywhere.model.UserInfo;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by TranThanhTung on 19/09/2017.
 */

public interface GetUserInfoService {
    @GET("api/Auth/Users/{id}")
    Observable<UserInfo> getUserInfo(@Header("access_token") String accessToken, @Path("id") String id);
}
