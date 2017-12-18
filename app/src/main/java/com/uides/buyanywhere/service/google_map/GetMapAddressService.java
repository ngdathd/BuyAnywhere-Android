package com.uides.buyanywhere.service.google_map;

import com.uides.buyanywhere.model.google_map.GoogleAddressResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by TranThanhTung on 17/12/2017.
 */

public interface GetMapAddressService {
    @GET("maps/api/geocode/json")
    Observable<GoogleAddressResponse> getAddress(@Query("latlng") String latlon);
}
