package com.uides.buyanywhere.service.product;

import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.ProductPreview;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 27/11/2017.
 */

public interface FilterProductsService {
    @GET("api/Procurement/Products")
    Observable<PageResult<ProductPreview>> filterProducts(@Query("name") String name,
                                                          @Query("categoryName") String categoryName,
                                                          @Query("pageIndex") int pageIndex,
                                                          @Query("pageSize") Integer pageSize,
                                                          @Query("orderBy") String field,
                                                          @Query("orderType") String type);
}
