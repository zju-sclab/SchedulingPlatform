package com.skywilling.cn.common.model;


import lombok.Data;
import java.io.Serializable;


/**
 * ClassName LidarPoint
 * Author  Lin
 * Date 2019/4/8 21:48
 **/
@Data
public class LidarPoint implements Serializable {

    private double speed_limit;
    private int is_lane;
    private int lane_id;
    private int cross_id;
    private Position position;
    private Orientation orientation;

    public LidarPoint(){ }

    public LidarPoint(String px, String py, String pz, String ox, String oy, String oz, String ow){
       this.position = new Position(Double.valueOf(px),Double.valueOf(py),Double.valueOf(pz));
       this.orientation =  new Orientation(Double.valueOf(ox),Double.valueOf(oy),Double.valueOf(oz),Double.valueOf(ow));
    }
}
