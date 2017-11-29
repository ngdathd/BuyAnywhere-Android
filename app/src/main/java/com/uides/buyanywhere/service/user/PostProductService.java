package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.PostProduct;
import com.uides.buyanywhere.model.ProductReview;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public interface PostProductService {
    @POST("api/Procurement/Products")
    Observable<ProductReview> postProduct(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                          @Body PostProduct postProduct);
}
