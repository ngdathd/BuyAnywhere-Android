package com.uides.buyanywhere.service.shop;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 17/12/2017.
 */

public interface GetShopIDService {
    @GET("api/Procurement/Shops/{shopName}/Id")
    Observable<String> getShopID(@Path("shopName") String shopName);
}
