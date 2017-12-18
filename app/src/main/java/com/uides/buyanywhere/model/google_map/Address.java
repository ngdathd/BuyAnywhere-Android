package com.uides.buyanywhere.model.google_map;

import com.uides.buyanywhere.Constant;

/**
 * Created by TranThanhTung on 17/12/2017.
 */

public class Address {
    private String formatted_address;
    private Geometry geometry;

    public String getFormatted_address() {
        formatted_address = formatted_address.replaceAll(Constant.UNNAMED_ROAD + ", ", "");
        formatted_address = formatted_address.replaceAll(", " + Constant.VIETNAM, "");
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public String toString() {
        return "Address{" +
                "formatted_address='" + formatted_address + '\'' +
                ", geometry=" + geometry +
                '}';
    }
}
