package com.uides.buyanywhere.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.uides.buyanywhere.Constant;

/**
 * Created by TranThanhTung on 26/09/2017.
 */

public class UserAccessToken {
    private static String access_token;

    public static void saveUserAccessToken(Context context, String accessToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.ACCESS_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.ACCESS_TOKEN, accessToken);
        editor.apply();
        UserAccessToken.access_token = accessToken;
    }

    public static String getAccessToken(Context context) {
        if(access_token != null){
            return access_token;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.ACCESS_TOKEN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constant.ACCESS_TOKEN, null);
    }
}
