package com.uides.buyanywhere.network.service;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.ProductOrder;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by TranThanhTung on 02/10/2017.
 */

public interface ProductOrderService {
    @GET("api/Procurement/PurchaseOrders")
    Observable<List<ProductOrder>> getProductOrders(@Header(Constant.ACCESS_TOKEN) String accessToken);

    @GET("api/Procurement/PurchaseOrders/{purchaseOrderId}")
    Observable<ProductOrder> getProductOrder(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                             @Path("purchaseOrderId") String id);
}
