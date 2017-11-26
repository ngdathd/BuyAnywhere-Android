package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.Category;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by TranThanhTung on 24/11/2017.
 */

public interface GetFavoriteCategoriesService {
    @GET("api/Procurement/Users/current/FavoriteCategories")
    Observable<List<Category>> getFavoriteCategories(@Header(Constant.ACCESS_TOKEN) String accessToken);
}
