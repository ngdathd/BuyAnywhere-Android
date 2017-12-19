package com.uides.buyanywhere.ui.fragment.auth;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.iid.FirebaseInstanceId;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.custom_view.dialog.MessageDialog;
import com.uides.buyanywhere.model.User;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.auth.LoginService;
import com.uides.buyanywhere.ui.activity.AuthActivity;
import com.uides.buyanywhere.ui.activity.MainActivity;
import com.uides.buyanywhere.utils.SharedPreferencesOpenHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Admin on 7/2/2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult>, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "LoginFragment";

    @BindView(R.id.txt_input_email)
    TextInputLayout inputEmail;
    @BindView(R.id.txt_input_password)
    TextInputLayout inputPassword;
    @BindView(R.id.edt_email)
    EditText email;
    @BindView(R.id.edt_password)
    EditText password;
    @BindView(R.id.check_box_remember)
    CheckBox rememberCheckBox;
    @BindView(R.id.check_box_auto_sign_in)
    CheckBox autoSignInCheckBox;

    private boolean isAutoSignIn;

    private LoadingDialog loadingDialog;
    private CompositeDisposable compositeDisposable;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> facebookCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        isAutoSignIn = getArguments().getBoolean(Constant.KEY_AUTO_SIGN_IN, true);
        initFacebookSignIn();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);
        initViews(rootView);
        return rootView;
    }

    private void saveAccountAndStartMainActivity() {
        //save new email to local
        String email;
        String password;
        if (rememberCheckBox.isChecked()) {
            email = this.email.getText().toString().trim();
            password = this.password.getText().toString();
        } else {
            email = "";
            password = "";
        }
        Context context = getActivity();
        SharedPreferencesOpenHelper.saveSignIn(context, email, password);

        //show main activity
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void initViews(View rootView) {
        rootView.findViewById(R.id.btn_login).setOnClickListener(this);
        rootView.findViewById(R.id.btn_register).setOnClickListener(this);
        rootView.findViewById(R.id.btn_facebook).setOnClickListener(this);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isAutoSignIn) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken != null && accessToken.getToken() != null) {
                String token = accessToken.getToken();
                Log.i(TAG, "onStart: " + token);
                Context context = getActivity();
                if (SharedPreferencesOpenHelper.isAccessTokenValid(context, token) &&
                        SharedPreferencesOpenHelper.isCloudTokenValid(context, FirebaseInstanceId.getInstance().getToken())) {
                    Activity activity = getActivity();
                    UserAuth.setAuthUser(SharedPreferencesOpenHelper.getUser(context));
                    Intent intent = new Intent(activity, MainActivity.class);
                    startActivity(intent);
                    activity.finish();
                } else {
                    signIn(accessToken.getToken());
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        String acc;
//        String pass;
//        Bundle bundle = getArguments();
//        if (bundle == null) {
//            SignInInfo signInInfo = SharedPreferencesOpenHelper.getSignInInfo(getActivity());
//            acc = signInInfo.getUsername();
//            pass = signInInfo.getPassword();
//            autoSignInCheckBox.setChecked(signInInfo.isAutoSignIn());
//        } else {
//            acc = bundle.getString(Constant.KEY_EMAIL, "");
//            pass = bundle.getString(Constant.KEY_PASSWORD, "");
//        }
//
//        email.setText(acc);
//        password.setText(pass);
//
//        rememberCheckBox.setChecked(!acc.isEmpty());
//
//        if (!disableAutoSignInOnce && autoSignInCheckBox.isChecked()) {
//            signIn(email.getText().toString(), password.getText().toString());
//            disableAutoSignInOnce = false;
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferencesOpenHelper.saveAutoSignInFlag(getActivity(), autoSignInCheckBox.isChecked());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private boolean validateAccount(String account) {
        if (account.isEmpty()) {
            inputEmail.setError(getString(R.string.error_account));
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(account).matches()) {
            inputEmail.setError(getString(R.string.account_not_exist));
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            inputPassword.setError(getString(R.string.error_password));
            return false;
        }
        return true;
    }

    public void signIn(String account, String password) {
        account = account.trim();
        if (!validateAccount(account)) {
            return;
        }

        if (!validatePassword(password)) {
            return;
        }

        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(account, password).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        loadingDialog.dismiss();
        if (task.isSuccessful()) {
            FirebaseAuth.getInstance().addAuthStateListener(this);
            saveAccountAndStartMainActivity();
        } else {
            new MessageDialog(getActivity(), "Sign in failed", task.getResult().toString(), null).show();
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            for (UserInfo userInfo : user.getProviderData()) {
                Log.i(TAG, userInfo.getDisplayName() + " " + userInfo.getPhotoUrl());
            }
        }
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    private void initFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create();

        facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final String accessToken = loginResult.getAccessToken().getToken();
                Log.i(TAG, "onFacebookSuccess: " + accessToken);
                signIn(accessToken);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "onFacebookError: " + error);
                Toast.makeText(getActivity(), R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
    }

    private void signIn(String token) {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        Network network = Network.getInstance();
        String cloudToken = FirebaseInstanceId.getInstance().getToken();
        LoginService loginService = network.createService(LoginService.class);
        Disposable disposable = loginService.facebookSignIn(token, cloudToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> onSignInSuccess(success, cloudToken), this::onSignInError);
        compositeDisposable.add(disposable);
    }

    private void onSignInSuccess(User user, String cloudToken) {
        loadingDialog.dismiss();
        Log.i(TAG, "onSignInSuccess: " + user);

        SharedPreferencesOpenHelper.saveUser(getActivity(), user);

        Activity activity = getActivity();
        UserAuth.setAuthUser(user);
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        activity.finish();
    }

    private void onSignInError(Throwable e) {
        loadingDialog.dismiss();
        Log.i(TAG, "onSignInError: " + e);
        Toast.makeText(getActivity(), R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: {
//                signIn(email.getText().toString(), password.getText().toString());
            }
            break;

            case R.id.btn_register: {
                ((AuthActivity) getActivity()).showRegisterFragment(email.getText().toString().trim());
            }
            break;

            case R.id.btn_facebook: {
                signInWithFacebook();
            }
            break;

            default: {
                break;
            }
        }
    }

    private void signInWithFacebook() {
        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();
        if (facebookAccessToken != null) {
            String accessToken = facebookAccessToken.getToken();
            if (accessToken != null) {
                signIn(accessToken);
                return;
            }
        }
        List<String> listPermissions = new ArrayList<>();
        listPermissions.add(Constant.PUBLIC_PROFILE);
        listPermissions.add(Constant.EMAIL);
        LoginManager.getInstance().logInWithReadPermissions(this, listPermissions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            autoSignInCheckBox.setVisibility(View.VISIBLE);
        } else {
            autoSignInCheckBox.setChecked(false);
            autoSignInCheckBox.setVisibility(View.INVISIBLE);
        }
    }
}
