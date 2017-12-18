package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.model.ShopLocationResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 18/12/2017.
 */

public interface GetProductByLocationService {
    @GET("api/Procurement/Shops")
    Observable<List<ShopLocationResult>> getProducts(@Query("productName") String productName,
                                                    @Query("lat") double lat,
                                                    @Query("lon") double lon,
                                                    @Query("distance") double distance);
}
