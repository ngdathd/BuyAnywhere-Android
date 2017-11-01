package com.uides.buyanywhere.network.service;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.Product;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by TranThanhTung on 02/10/2017.
 */

public interface ProductService {
    @GET("api/Procurement/Products")
    Observable<List<Product>> getProducts(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                          @Query("name") String name,
                                          @Query("skip") int skip,
                                          @Query("take") int take,
                                          @Query("orderBy") String field,
                                          @Query("orderType") int type);

    @POST("api/Procurement/Products")
    Observable<Product> createProduct(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                      @Body Product product);

    @GET("/api/Procurement/Products/{productId}")
    Observable<Product> getProduct(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                   @Path("productID") String productID);

    @PUT("/api/Procurement/Products/{productId}")
    Observable<Product> updateProduct(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                      @Path("productID") String productID,
                                      @Body Product product);
}
