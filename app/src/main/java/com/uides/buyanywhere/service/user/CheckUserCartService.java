package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public interface CheckUserCartService {
    @GET("api/Procurement/Users/current/Carts/Products/{id}/IsProductInCart")
    Observable<Boolean> containProduct(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                       @Path("id") String productID);
}
