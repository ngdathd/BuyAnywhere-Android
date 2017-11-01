package com.uides.buyanywhere.network.service;

import com.uides.buyanywhere.model.Category;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by TranThanhTung on 02/10/2017.
 */

public interface CategoriesService {
    @GET("api/Procurement/Categories")
    Observable<List<Category>> getCategories();
}
