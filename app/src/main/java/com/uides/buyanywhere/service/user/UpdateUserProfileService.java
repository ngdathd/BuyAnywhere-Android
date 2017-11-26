package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.UserProfile;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;

/**
 * Created by TranThanhTung on 24/11/2017.
 */

public interface UpdateUserProfileService {
    @PATCH("api/Auth/Users/current")
    Observable<Object> updateUserProfile(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                         @Body UserProfile userProfile);
}
