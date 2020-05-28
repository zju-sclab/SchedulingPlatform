package com.skywilling.cn.livemap.model;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName CarArrivalslnfo
 * Author  Lin
 * Date 2019/5/5 16:25
 **/

@Data

public class CarArrivalslnfo implements Serializable {
    private String vin;
    private long timestamp;
    public CarArrivalslnfo(){}
    public CarArrivalslnfo(String vin, long timestamp){
        this.setVin(vin);
        this.setTimestamp(timestamp);
    }
}
