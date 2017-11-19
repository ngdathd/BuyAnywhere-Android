package com.uides.buyanywhere.service;

import com.uides.buyanywhere.Constant;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;

/**
 * Created by TranThanhTung on 26/09/2017.
 */

public interface RegisterGCMTokenService {
    @PUT("api/UserAuth/Users/current/CloundToken")
    Observable<Object> register(@Header(Constant.ACCESS_TOKEN) String userToken,
                                @Body String cloudMessageToken);
}
