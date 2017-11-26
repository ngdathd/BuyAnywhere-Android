package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by TranThanhTung on 25/11/2017.
 */

public interface DeleteFavoriteCategoriesService {
    @POST("api/Procurement/Users/current/FavoriteCategories/Deleted")
    Observable<Object> deleteFavoriteCategories(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                                @Body List<String> categoryIDs);
}
