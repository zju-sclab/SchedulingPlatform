package com.skywilling.cn.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GeoLocation implements Serializable {
    private static final long serialVersionUID = 4446937736823413804L;
    private double longitude;
    private double latitude;
    private double altitude;

    public GeoLocation(){

    }
    public GeoLocation(double longitude, double latitude, double altitude){
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

}
