package com.uides.buyanywhere.model.google_map;

import java.util.List;

/**
 * Created by TranThanhTung on 17/12/2017.
 */

public class GoogleAddressResponse {
    private List<Address> results;

    public List<Address> getResults() {
        return results;
    }

    public void setResults(List<Address> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "GoogleAddressResponse{" +
                "results=" + results +
                '}';
    }
}
