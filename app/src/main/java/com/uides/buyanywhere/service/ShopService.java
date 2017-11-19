package com.uides.buyanywhere.service;

import com.uides.buyanywhere.model.Shop;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 08/11/2017.
 */

public interface ShopService {

    @GET("api/Procurement/Shops")
    Observable<List<Shop>> get();

    @POST("api/Procurement/Shops")
    Observable<Shop> post(@Header("access_token") String token, @Body Shop shop);

    @PUT("api/Procurement/Shops/{id}")
    Observable<Shop> put(@Header("access_token") String token, @Path("id") String id, @Body Shop shop);
}