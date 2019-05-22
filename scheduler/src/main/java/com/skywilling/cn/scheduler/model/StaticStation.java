package com.skywilling.cn.scheduler.model;

import com.skywilling.cn.common.model.Pose;
import lombok.Data;

@Data
public class StaticStation {
    private String name;
    private String type;
    private int id;

    private Pose point;

    public StaticStation(){
        point = new Pose();
    }

    public void setPoint(double px, double py, double pz, double ox, double oy, double oz, double ow){
        point.setPosition(px, py, pz);
        point.setOrientation(ox,oy,oz,ow);
    }
}
