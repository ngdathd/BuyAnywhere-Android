package com.uides.buyanywhere.ui.activity;

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
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.model.User;
import com.uides.buyanywhere.service.LoginService;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.utils.UserAccessToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    private CompositeDisposable compositeDisposable;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> facebookCallback;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDebugMode(true);

        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
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

    private void init() {
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

        compositeDisposable = new CompositeDisposable();
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
        GoogleSignInAccount userAccount = result.getSignInAccount();
        if (result.isSuccess() && userAccount != null) {
            Log.i(TAG, "handleSignInResult: " + userAccount.getIdToken());
        } else {
            Toast.makeText(this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn(String token) {
        LoginService loginService = Network.getInstance().createService(LoginService.class);
        Disposable disposable = loginService.facebookSignIn(token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSignInSuccess, this::onSignInError);
    }

    private void onSignInError(Throwable e) {
        Toast.makeText(this, R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    private void onSignInSuccess(User user) {
        UserAuth.setAuthUser(user);
        navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
