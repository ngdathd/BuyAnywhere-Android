package com.uides.buyanywhere.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.ui.fragment.auth.LoginFragment;
import com.uides.buyanywhere.ui.fragment.auth.RegisterFragment;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 7/1/2017.
 */

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = "AuthActivity";
    private static final String BACK_TO_LOGIN = "BACK_TO_LOGIN";
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
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
        getFragmentManager().beginTransaction()
                .replace(R.id.fl_main, loginFragment)
                .commit();
    }

    public void showLogInFragment(String account, String password) {
        Bundle bundle = new Bundle();
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
