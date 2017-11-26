package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.Shop;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public interface GetOwnerShop {
    @GET("api/Procurement/Users/current/Shops")
    Observable<Shop> getOwnerShop(@Header(Constant.ACCESS_TOKEN) String accessToken);
}
