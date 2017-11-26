package com.uides.buyanywhere.model;

/**
 * Created by Admin on 7/3/2017.
 */

public class SignInInfo {
    private String username;
    private String password;
    private boolean autoSignIn;

    public SignInInfo(String username, String password, boolean autoSignIn) {
        this.username = username;
        this.password = password;
        this.autoSignIn = autoSignIn;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAutoSignIn() {
        return autoSignIn;
    }
}
