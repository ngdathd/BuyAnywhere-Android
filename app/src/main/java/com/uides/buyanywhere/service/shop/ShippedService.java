package com.uides.buyanywhere.service.shop;

import com.uides.buyanywhere.model.ShopOrder;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 09/12/2017.
 */

public interface ShippedService {
    @POST("api/Orders/{orderId}/ShippedDate")
    Observable<ShopOrder> ship(@Path("orderId") String orderID);
}
