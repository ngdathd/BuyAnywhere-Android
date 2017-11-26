package com.uides.buyanywhere.service.user;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.Shop;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public interface UpdateOwnerShopService {
    @PATCH("api/Procurement/Users/current/Shops")
    Observable<Object> updateOwnerShop(@Header(Constant.ACCESS_TOKEN) String accessToken,
                                       @Body Shop shop);
}
