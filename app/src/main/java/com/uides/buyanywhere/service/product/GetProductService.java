package com.uides.buyanywhere.service.product;

import com.uides.buyanywhere.model.Product;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by TranThanhTung on 24/11/2017.
 */

public interface GetProductService {
    @GET("api/Procurement/Products/{id}")
    Observable<Product> getProduct(@Path("id") String productID);
}
