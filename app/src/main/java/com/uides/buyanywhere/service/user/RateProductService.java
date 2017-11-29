package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.Rating;
import com.uides.buyanywhere.model.RatingResult;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 27/11/2017.
 */

public interface RateProductService {
    @PUT("api/Procurement/Users/current/Products/{productId}/Ratings")
    Observable<RatingResult> rateProduct(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                         @Path("productId") String productID,
                                         @Body Rating rating);
}
