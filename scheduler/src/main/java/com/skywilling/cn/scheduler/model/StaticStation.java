package com.skywilling.cn.scheduler.model;

import com.skywilling.cn.common.model.Pose;
import lombok.Data;

import java.io.Serializable;

@Data
public class StaticStation implements Serializable {
    private String name;
    private String type;
    private int id;

    private Pose point;
    public StaticStation(){setPoint(new Pose());}

    public StaticStation(Pose pose){
        setPoint(pose);
    }

    public StaticStation(double px, double py, double pz, double ox, double oy, double oz, double ow){
        setPoint(new Pose());
        point.setPosition(px, py, pz);
        point.setOrientation(ox,oy,oz,ow);
    }
    public void setPoint(double px, double py, double pz, double ox, double oy, double oz, double ow){
        point.setPosition(px, py, pz);
        point.setOrientation(ox,oy,oz,ow);
    }
}
