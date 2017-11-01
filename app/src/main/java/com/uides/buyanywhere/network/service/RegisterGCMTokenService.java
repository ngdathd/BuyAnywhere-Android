package com.uides.buyanywhere.network.service;

import com.uides.buyanywhere.Constant;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import rx.Observable;

/**
 * Created by TranThanhTung on 26/09/2017.
 */

public interface RegisterGCMTokenService {
    @PUT("api/Auth/Users/current/CloundToken")
    Observable<Object> register(@Header(Constant.ACCESS_TOKEN) String userToken,
                                @Body String cloudMessageToken);
}
