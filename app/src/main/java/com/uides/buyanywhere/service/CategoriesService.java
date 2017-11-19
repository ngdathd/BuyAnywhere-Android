package com.uides.buyanywhere.service;

import com.uides.buyanywhere.model.Category;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by TranThanhTung on 02/10/2017.
 */

public interface CategoriesService {
    @GET("api/Procurement/Categories")
    Observable<List<Category>> getCategories();
}
