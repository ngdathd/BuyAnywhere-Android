package com.uides.buyanywhere.network.retrofit;

import com.uides.buyanywhere.Constant;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
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
        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.BASE_URL)
                .build();
    }
}
