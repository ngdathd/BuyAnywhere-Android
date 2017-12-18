package com.uides.buyanywhere.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uides.buyanywhere.Constant;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TranThanhTung on 17/12/2017.
 */

public class GoogleMapClient {
    private static GoogleMapClient instance = new GoogleMapClient();
    private Retrofit retrofit;

    public static GoogleMapClient getInstance(){
        return instance;
    }

    public <T> T createService(Class<T> serviceClass){
        return retrofit.create(serviceClass);
    }

    private GoogleMapClient(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("http://maps.googleapis.com")
                .build();
    }
}
