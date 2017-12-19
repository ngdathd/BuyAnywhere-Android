package com.uides.buyanywhere.service.product;

import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.ProductPreview;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 02/10/2017.
 */

public interface GetProductsService {
    @GET("api/Procurement/Products")
    Observable<PageResult<ProductPreview>> getProducts(@Query("pageIndex") int pageIndex,
                                                       @Query("pageSize") Integer pageSize,
                                                       @Query("orderBy") String field,
                                                       @Query("orderType") int type);
}
