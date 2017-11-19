package com.uides.buyanywhere.auth;

import com.uides.buyanywhere.model.User;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class UserAuth {
    private static User user;

    public static User getAuthUser() {
        return user;
    }

    public static void setAuthUser(User authUser) {
        user = authUser;
    }
}
