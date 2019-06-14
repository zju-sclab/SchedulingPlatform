package com.skywilling.cn.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName Order
 * Author  Lin
 * Date 2019/6/10 9:36
 **/
@Data
public class Order implements Serializable {
    private String orderId;
    private String username;
    private String startHour;
    private String startMinute;
    private String outset;
    private String destination;
    private String parkName;
    public Order(){}
    public Order(String username,String startHour, String startMinute,String outset,String destination,String parkName){
        this.username = username;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.outset = outset;
        this.destination = destination;
        this.parkName = parkName;
    }
}
