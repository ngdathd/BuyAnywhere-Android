package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.UserProfile;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by TranThanhTung on 22/11/2017.
 */

public interface GetUserProfileService {
    @GET("api/Auth/Users/current")
    Observable<UserProfile> getUserProfile(@Header(Constant.ACCESS_TOKEN) String accessToken);
}
