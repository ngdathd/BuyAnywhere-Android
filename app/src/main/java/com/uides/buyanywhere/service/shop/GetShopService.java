package com.uides.buyanywhere.service.shop;

import com.uides.buyanywhere.model.Shop;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 08/11/2017.
 */

public interface GetShopService {
    @GET("api/Procurement/Shops/{shopId}")
    Observable<Shop> getShop(@Header("access_token") String token,
                             @Path("shopId") String id);
}