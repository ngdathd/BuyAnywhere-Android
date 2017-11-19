package com.uides.buyanywhere.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uides.buyanywhere.Constant;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TranThanhTung on 19/09/2017.
 */

public class Network {
    private static Network instance = new Network();
    private Retrofit retrofit;
    
    public static Network getInstance(){
        return instance;
    }

    public <T> T createService(Class<T> serviceClass){
        return retrofit.create(serviceClass);
    }
    
    private Network(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constant.BASE_URL)
                .build();
    }
}
