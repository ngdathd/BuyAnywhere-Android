package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.model.UserProfile;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 18/12/2017.
 */

public interface GetUserService {
    @GET("api/Auth/Users/{userId}")
    Observable<UserProfile> getUserProfile(@Path("userId") String userID);
}
