package com.uides.buyanywhere.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.main.MainActivity;
import com.uides.buyanywhere.model.UserInfo;
import com.uides.buyanywhere.network.service.LogInService;
import com.uides.buyanywhere.network.retrofit.Network;
import com.uides.buyanywhere.utils.UserAccessToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 15/09/2017.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 0;
    private String TAG = getClass().getSimpleName();

    @BindView(R.id.btn_facebook)
    Button facebookButton;
    @BindView(R.id.btn_google)
    Button googleButton;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> facebookCallback;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDebugMode(true);

        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        initViews();
    }

    public void setDebugMode(boolean enable) {
        if (enable) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        } else {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.removeLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
    }

    private void initViews() {
        prepareFacebookSignIn();
        prepareGoogleSignIn();

        if (isUserSignedInFacebook()) {
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, facebookCallback);
            List<String> listPermissions = new ArrayList<>();
            listPermissions.add(Constant.PUBLIC_PROFILE);
            listPermissions.add(Constant.EMAIL);
            loginManager.logInWithReadPermissions(this, listPermissions);
        }
    }

    private void prepareGoogleSignIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.google_client_id))
                .build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
        googleButton.setOnClickListener(this);
    }

    private void prepareFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create();

        facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final String accessToken = loginResult.getAccessToken().getToken();
                UserAccessToken.saveUserAccessToken(SignInActivity.this, accessToken);
                signIn(accessToken);
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
            }
        };

        facebookButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_facebook: {
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.registerCallback(callbackManager, facebookCallback);
                loginManager.logInWithReadPermissions(this, Collections.singletonList(Constant.PUBLIC_PROFILE));
            }
            break;

            case R.id.btn_google: {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
            break;

            default: {
                break;
            }
        }
    }

    private boolean isUserSignedInFacebook() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN: {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
            break;

            default: {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount userAccount = result.getSignInAccount();
            Log.i(TAG, "handleSignInResult: " + userAccount.getIdToken());
//            signIn(userAccount.getIdToken());
        } else {
            Toast.makeText(this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn(String token) {
        LogInService logInService = Network.getInstance().createService(LogInService.class);
        Observable<UserInfo> signInServiceObservable = logInService.facebookSignIn(token);
        signInServiceObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        navigateToMainActivity(userInfo);
                    }
                });
    }

    private void navigateToMainActivity(UserInfo userInfo) {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra(Constant.USER_INFO, userInfo);
        startActivity(intent);
        finish();
    }
}
