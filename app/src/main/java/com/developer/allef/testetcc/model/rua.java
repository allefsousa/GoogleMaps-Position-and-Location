package com.developer.allef.testetcc.model;

import java.io.Serializable;

/**
 * Created by Allef on 15/02/2017.
 */

public class rua implements Serializable{
    public static final long  serialVersionUID = 100L;
    Double latitude;
    Double longitude;

    public rua() {
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
