package com.uides.buyanywhere.model.google_map;

/**
 * Created by TranThanhTung on 18/12/2017.
 */

public class Geometry {
    Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Geometry{" +
                "location=" + location +
                '}';
    }
}
