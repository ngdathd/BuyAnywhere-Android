package com.uides.buyanywhere.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.ui.fragment.auth.LoginFragment;
import com.uides.buyanywhere.ui.fragment.auth.RegisterFragment;

/**
 * Created by Admin on 7/1/2017.
 */

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = "AuthActivity";
    private static final String BACK_TO_LOGIN = "BACK_TO_LOGIN";
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private boolean isAutoSignIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        isAutoSignIn = getIntent().getBooleanExtra(Constant.KEY_AUTO_SIGN_IN, true);
        initViews();
        showLoginFragment();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initViews() {
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
    }

    public void showLoginFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.KEY_AUTO_SIGN_IN, isAutoSignIn);
        loginFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.fl_main, loginFragment)
                .commit();
    }

    public void showLogInFragment(String account, String password) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.KEY_AUTO_SIGN_IN, isAutoSignIn);
        bundle.putString(Constant.KEY_EMAIL, account);
        bundle.putString(Constant.KEY_PASSWORD, password);
        loginFragment.setArguments(bundle);
        showLoginFragment();
    }

    public void showRegisterFragment(String account) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_EMAIL, account);
        registerFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.fl_main, registerFragment).addToBackStack(BACK_TO_LOGIN)
                .commit();
    }
}
