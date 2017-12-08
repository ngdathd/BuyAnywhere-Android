package com.uides.buyanywhere.service.auth;

import com.uides.buyanywhere.model.User;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 18/09/2017.
 */

public interface LoginService {
    @POST("api/Auth/OAuth2/Facebook/Callback")
    Observable<User> facebookSignIn(@Body String accessToken,
                                    @Query("cloundToken") String cloudToken);
}
