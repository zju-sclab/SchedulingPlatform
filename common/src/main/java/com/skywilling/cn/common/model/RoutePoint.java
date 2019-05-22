package com.skywilling.cn.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoutePoint implements Serializable, Cloneable {
    private double speed_limit;
    private int is_lane;
    private int lane_id;
    private int cross_id;
    private Position position;
    private Orientation orientation;

    public RoutePoint(){}
    @Override
    public Object clone(){
        RoutePoint rc = null;
        try{
            rc = (RoutePoint)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return rc;
    }
}
