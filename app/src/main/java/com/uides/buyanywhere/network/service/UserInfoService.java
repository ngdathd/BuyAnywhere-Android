package com.uides.buyanywhere.network.service;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.model.UserInfo;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by TranThanhTung on 19/09/2017.
 */

public interface UserInfoService {
    @GET("api/Auth/Users/{id}")
    Observable<UserInfo> getUserInfo(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                     @Path("id") String id);

    @PATCH("api/Auth/Users/current")
    Observable<UserInfo> updateUserInfo(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                        @Body UserInfo userInfo);

    @GET("/api/Procurement/Users/current/FavoriteCategories")
    Observable<List<Category>> getFavoriteCategories(@Header(Constant.ACCESS_TOKEN) String accessToken);

    @PUT("api/Procurement/Users/current/FavoriteCategories/{listCategoriesId}")
    Observable<Object> addFavoriteCategories(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                             @Path("listCategoriesId") List<Category> listCategories);
}
