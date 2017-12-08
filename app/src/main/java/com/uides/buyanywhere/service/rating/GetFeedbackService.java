package com.uides.buyanywhere.service.rating;

import com.uides.buyanywhere.model.Feedback;
import com.uides.buyanywhere.model.PageResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 02/12/2017.
 */

public interface GetFeedbackService {
    @GET("/api/Procurement/Products/{productId}/Ratings")
    Observable<PageResult<Feedback>> getAllFeedback(@Path("productId") String productID,
                                                 @Query("pageIndex") int pageIndex,
                                                 @Query("pageSize") Integer pageSize,
                                                 @Query("orderBy") String field,
                                                 @Query("orderType") String type);
}
