package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class PageResult<T> {
    @SerializedName("results")
    @Expose
    private List<T> results;
    @SerializedName("PageIndex")
    @Expose
    private int pageIndex;
    @SerializedName("PageSize")
    @Expose
    private int pageSize;
    @SerializedName("totalPages")
    @Expose
    private int totalPages;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
