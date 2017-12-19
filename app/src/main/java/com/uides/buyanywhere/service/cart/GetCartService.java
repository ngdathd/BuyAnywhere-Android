package com.uides.buyanywhere.service.cart;

import android.content.Intent;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.ProductPreview;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 21/11/2017.
 */

public interface GetCartService {
    @GET("api/Procurement/Users/current/Carts")
    Observable<PageResult<ProductPreview>> getProductsInCart(@Header(Constant.ACCESS_TOKEN) String userToken,
                                                             @Query("pageIndex") Intent pageIndex,
                                                             @Query("pageSize") Integer pageSize,
                                                             @Query("orderBy") String field,
                                                             @Query("orderType") int type);



}
