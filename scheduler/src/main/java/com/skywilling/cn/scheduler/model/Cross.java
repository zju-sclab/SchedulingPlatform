package com.skywilling.cn.scheduler.model;

import com.skywilling.cn.common.model.LidarPoint;
import com.skywilling.cn.scheduler.common.utils;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class Cross implements Serializable, Cloneable, Comparable<Cross> {
//    private static final long serialVersionUID = -6776766621769031316L;
    private int id;
    private double length;
    private double weight;
    private double Culvature;
    private boolean reverse;
    private List<Integer> pre_id;
    private List<Integer> next_id;
    private List<Pose> points;

    public Cross(){
        pre_id = new ArrayList<>();
        next_id = new ArrayList<>();
        points = new ArrayList<>();
    }

    public void addPoints(Pose pose){
        points.add(pose);
    }

    public int getPointsNum(){
        return points.size();
    }

    public void addPre_id(int preid){
        this.pre_id.add(preid);
    }

    public void addNext_id(int next_id){
        this.next_id.add(next_id);
    }

    public Pose getFirstPoint(){
        return this.points.get(0);
    }

    public Pose getLastPoint(){
        return this.points.get(this.points.size()-1);
    }

    public boolean getReverse(){
        return this.reverse;
    }

    @Override
    public Object clone(){
        Cross cc = null;
        try{
            cc = (Cross)super.clone();
            cc.pre_id = utils.depcopy(this.pre_id);
            cc.next_id = utils.depcopy(this.next_id);
            cc.points = utils.depcopy(this.points);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cc;
    }

    @Override
    public int compareTo(Cross cross) {
        if(this.getId() < cross.getId()) return -1;
        else return 1;
    }
}
