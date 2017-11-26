package com.uides.buyanywhere.service.notification;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.User;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;

/**
 * Created by TranThanhTung on 02/10/2017.
 */

public interface EnableNotificationService {
    @PUT("api/UserAuth/Users/current/Notification")
    Observable<User> enableNotification(@Header(Constant.ACCESS_TOKEN) String token,
                                        @Body boolean enable);
}
