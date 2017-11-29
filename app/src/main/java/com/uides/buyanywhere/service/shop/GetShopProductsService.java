package com.uides.buyanywhere.service.shop;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.ProductReview;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 27/11/2017.
 */

public interface GetShopProductsService {
    @GET("api/Procurement/Shops/{shopId}/Products")
    Observable<PageResult<ProductReview>> getShopProducts(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                                          @Path("shopId") String shopID,
                                                          @Query("pageIndex") int pageIndex,
                                                          @Query("pageSize") Integer pageSize,
                                                          @Query("orderBy") String field,
                                                          @Query("orderType") int type);
}
