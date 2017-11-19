package com.uides.buyanywhere.service;

import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.ProductReview;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 02/10/2017.
 */

public interface GetProductReviewsService {
    @GET("api/Procurement/Products")
    Observable<PageResult<ProductReview>> getProducts(@Query("pageIndex") int pageIndex,
                                                      @Query("pageSize") Integer pageSize,
                                                      @Query("orderBy") String field,
                                                      @Query("orderType") int type);
}
