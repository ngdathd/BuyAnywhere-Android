package com.uides.buyanywhere.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 15/09/2017.
 */

public class SignInActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    @BindView(R.id.login_button) LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDebugMode(true);

        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        initViews();
    }

    public void setDebugMode(boolean enable){
        if(enable) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }else {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.removeLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
    }

    private void initViews() {
        callbackManager = CallbackManager.Factory.create();

        FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess: "+ loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "onError: "+error.getMessage());
            }
        };

        if(isUserSignedIn()){
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, facebookCallback);
            loginManager.logInWithReadPermissions(this, Collections.singletonList(Constant.PUBLIC_PROFILE));
        }else {
            loginButton.setReadPermissions(Constant.PUBLIC_PROFILE);
            loginButton.registerCallback(callbackManager,facebookCallback);
        }
    }

    private boolean isUserSignedIn(){
        return AccessToken.getCurrentAccessToken() != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
