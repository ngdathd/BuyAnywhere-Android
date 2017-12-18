package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.OrderBody;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 17/12/2017.
 */

public interface CreateOrderService {
    @POST("/api/Procurement/Users/{userId}/Orders")
    Observable<Object> createOrder(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                   @Path("userId") String userID,
                                   @Body OrderBody orderBody);
}
