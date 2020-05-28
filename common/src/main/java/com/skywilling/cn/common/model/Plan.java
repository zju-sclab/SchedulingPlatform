package com.skywilling.cn.common.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName Plan
 * Author  Lin
 * Date 2019/6/10 9:40
 **/

public class Plan implements Serializable {
    private String id;
    private String userName;
    private String startTime;
    private String outset;
    private String destination;
    private String parkName;
    private List<Double> useTimes;
    private List<String> sequence;
    public Plan(){}

    public Plan(Order order, List<Double> time, List<String> curves){
        this.startTime = order.getStartHour() + ":" + order.getStartMinute();
        this.parkName = order.getParkName();
        this.outset = order.getOutset();
        this.destination = order.getDestination();
        this.userName = order.getUsername();
        this.id = userName + "_" + System.currentTimeMillis();
        useTimes = new ArrayList<>();
        for(double val : time){
            BigDecimal bg = new BigDecimal(val);
            double new_val = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            useTimes.add(new_val);
        }
        sequence = curves;
        sequence.remove(0);
        sequence.remove(sequence.size()-1);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getOutset() {
        return outset;
    }

    public void setOutset(String outset) {
        this.outset = outset;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public List<Double> getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(List<Double> useTimes) {
        this.useTimes = useTimes;
    }

    public List<String> getSequence() {
        return sequence;
    }

    public void setSequence(List<String> sequence) {
        this.sequence = sequence;
    }
}
