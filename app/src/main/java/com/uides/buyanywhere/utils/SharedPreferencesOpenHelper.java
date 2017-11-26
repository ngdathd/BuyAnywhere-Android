package com.uides.buyanywhere.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.SignInInfo;

/**
 * Created by Admin on 7/3/2017.
 */

public class SharedPreferencesOpenHelper {
    public static final String SAVED_ACCOUNT_SHARE_PREFERENCES_NAME = "savedAccount";

    public static void saveSignIn(Context context, String email, String password){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.KEY_EMAIL, email);
        editor.putString(Constant.KEY_PASSWORD, password);
        editor.apply();
    }

    public static void saveAutoSignInFlag(Context context, boolean isAutoSignIn){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putBoolean(Constant.KEY_AUTO_SIGN_IN, isAutoSignIn)
                .apply();
    }

    public static SignInInfo getSignInInfo(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Constant.KEY_EMAIL, "");
        String password = sharedPreferences.getString(Constant.KEY_PASSWORD, "");
        boolean autoSignIn = sharedPreferences.getBoolean(Constant.KEY_AUTO_SIGN_IN, false);
        return new SignInInfo(username, password, autoSignIn);
    }
}
