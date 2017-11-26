package com.uides.buyanywhere.service.cart;

import com.uides.buyanywhere.Constant;

import io.reactivex.Observable;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 21/11/2017.
 */

public interface AddCartService {
    @POST("api/Procurement/Users/current/Carts/{productId}")
    Observable<Object> addProductToCart(@Header(Constant.ACCESS_TOKEN) String userToken,
                                        @Path("productId") String productID);
}
