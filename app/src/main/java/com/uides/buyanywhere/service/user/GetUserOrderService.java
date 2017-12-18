package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.UserOrder;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 18/12/2017.
 */

public interface GetUserOrderService {
    @GET("api/Users/{userId}/Orders")
    Observable<PageResult<UserOrder>> getUserOrder(@Path("userId") String userID,
                                        @Query("pageIndex") int pageIndex,
                                        @Query("pageSize") Integer pageSize,
                                        @Query("orderBy") String field,
                                        @Query("orderType") String type);
}
