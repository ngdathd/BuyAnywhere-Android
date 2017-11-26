package com.uides.buyanywhere.ui.fragment.auth;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.custom_view.dialog.MessageDialog;
import com.uides.buyanywhere.ui.activity.AuthActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 7/2/2017.
 */

public class RegisterFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "RegisterFragment";

    @BindView(R.id.txt_input_name)
    TextInputLayout inputName;
    @BindView(R.id.txt_input_email)
    TextInputLayout inputEmail;
    @BindView(R.id.txt_input_password)
    TextInputLayout inputPassword;
    @BindView(R.id.txt_input_confirm_password)
    TextInputLayout inputConfirmPassword;
    @BindView(R.id.edt_name)
    EditText fullName;
    @BindView(R.id.edt_email)
    EditText email;
    @BindView(R.id.edt_password)
    EditText password;
    @BindView(R.id.edt_confirm_password)
    EditText confirmPassword;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        ButterKnife.bind(this, rootView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            email.setText(bundle.getString(Constant.KEY_EMAIL));
        }

        rootView.findViewById(R.id.btn_register).setOnClickListener(this);
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
        String confirmPass = confirmPassword.getText().toString();
        if (!confirmPass.equals(password)) {
            inputConfirmPassword.setError(getString(R.string.password_not_match));
            return false;
        }
        return true;
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            inputName.setError(getString(R.string.empty_name));
            return false;
        }
        return true;
    }

    private void registerAccount(String name, String account, final String password) {
        final String finalName = name.trim();
        final String finalAccount = account.trim();

        if (!validateName(name)) {
            return;
        }

        if (!validateAccount(account)) {
            return;
        }

        if (!validatePassword(password)) {
            return;
        }

        progressDialog = ProgressDialog.show(getActivity(), "", "Processing", true);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(account, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ((AuthActivity) getActivity()).showLogInFragment(account, password);
                } else {
                    progressDialog.dismiss();
                    new MessageDialog(getActivity(), "Register failed", task.getResult().toString(), null).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register: {
                registerAccount(fullName.getText().toString(),
                        email.getText().toString(),
                        password.getText().toString());
            }
            break;

            default: {
                break;
            }
        }
    }
}
