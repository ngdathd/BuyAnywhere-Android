package com.uides.buyanywhere.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.model.SignInInfo;
import com.uides.buyanywhere.model.User;
import com.uides.buyanywhere.model.UserProfile;

/**
 * Created by Admin on 7/3/2017.
 */

public class SharedPreferencesOpenHelper {
    public static final String SAVED_ACCOUNT_SHARE_PREFERENCES_NAME = "user_profile";

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

    public static void saveUser(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(Constant.KEY_USER_ID, user.getId())
                .putString(Constant.KEY_SHOP_ID, user.getShopID())
                .putString(Constant.KEY_ACCESS_TOKEN, user.getAccessToken())
                .putString(Constant.KEY_CLOUD_TOKEN, user.getCloudToken())
                .putString(Constant.KEY_NAME, user.getName())
                .putString(Constant.KEY_EMAIL, user.getEmail())
                .putString(Constant.KEY_AVATAR, user.getAvatarUrl())
                .putString(Constant.KEY_COVER, user.getCoverUrl())
                .apply();
    }

    public static void saveUserProfile(Context context,
                                       String name,
                                       String email,
                                       String avatar,
                                       String cover) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(Constant.KEY_NAME, name)
                .putString(Constant.KEY_EMAIL, email)
                .putString(Constant.KEY_AVATAR, avatar)
                .putString(Constant.KEY_COVER, cover)
                .apply();
    }

    public static User getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(Constant.KEY_USER_ID, null);
        String shopID = sharedPreferences.getString(Constant.KEY_SHOP_ID, null);
        String accessToken = sharedPreferences.getString(Constant.KEY_ACCESS_TOKEN, null);
        String cloudToken = sharedPreferences.getString(Constant.KEY_CLOUD_TOKEN, null);
        String name = sharedPreferences.getString(Constant.KEY_NAME, null);
        String email = sharedPreferences.getString(Constant.KEY_EMAIL, null);
        String avatar = sharedPreferences.getString(Constant.KEY_AVATAR, null);
        String cover = sharedPreferences.getString(Constant.KEY_COVER, null);
        User user = new User();
        user.setId(id);
        user.setShopID(shopID);
        user.setAccessToken(accessToken);
        user.setCloudToken(cloudToken);
        user.setName(name);
        user.setEmail(email);
        user.setAvatarUrl(avatar);
        user.setCoverUrl(cover);
        return user;
    }

    public static void saveCloudToken(Context context, String cloudToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(Constant.KEY_CLOUD_TOKEN, cloudToken)
                .apply();
    }

    public static boolean isCloudTokenValid(Context context, String cloudToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constant.KEY_CLOUD_TOKEN, "").equals(cloudToken);
    }

    public static boolean isAccessTokenValid(Context context, String accessToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constant.KEY_ACCESS_TOKEN, "").equals(accessToken);
    }

    public static void saveAccessToken(Context context, String accessToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(Constant.KEY_ACCESS_TOKEN, accessToken)
                .apply();
    }

    public static String getShopID(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constant.KEY_SHOP_ID, null);
    }

    public static SignInInfo getSignInInfo(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVED_ACCOUNT_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Constant.KEY_EMAIL, "");
        String password = sharedPreferences.getString(Constant.KEY_PASSWORD, "");
        boolean autoSignIn = sharedPreferences.getBoolean(Constant.KEY_AUTO_SIGN_IN, false);
        return new SignInInfo(username, password, autoSignIn);
    }
}
