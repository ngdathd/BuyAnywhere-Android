package com.uides.buyanywhere.service;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.Category;
import com.uides.buyanywhere.model.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 19/09/2017.
 */

public interface UserService {
    @GET("api/UserAuth/Users/{id}")
    Observable<User> getUserInfo(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                 @Path("id") String id);

    @PATCH("api/UserAuth/Users/current")
    Observable<User> updateUserInfo(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                    @Body User user);

    @GET("/api/Procurement/Users/current/FavoriteCategories")
    Observable<List<Category>> getFavoriteCategories(@Header(Constant.ACCESS_TOKEN) String accessToken);

    @PUT("api/Procurement/Users/current/FavoriteCategories/{listCategoriesId}")
    Observable<Object> addFavoriteCategories(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                             @Path("listCategoriesId") List<Category> listCategories);
}
