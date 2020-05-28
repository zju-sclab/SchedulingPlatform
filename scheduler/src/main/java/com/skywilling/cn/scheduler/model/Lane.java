package com.skywilling.cn.scheduler.model;

import com.skywilling.cn.common.model.Pose;
import com.skywilling.cn.scheduler.common.utils;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Lane implements Serializable, Cloneable, Comparable<Lane> {
//    private static final long serialVersionUID = -6776766621769031316L;
    private int id;
    private double length;
    private double weight;
    private boolean reverse;
    private List<Integer> pre_id;
    private List<Integer> next_id;
    private List<Pose> points;

    public Lane(){
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

    public Pose getPoint(int index){
        return this.points.get(index);
    }

    public boolean getReverse(){
        return this.reverse;
    }

    @Override
    public Object clone(){
        Lane lc = null;
        try{
            lc = (Lane)super.clone();
            lc.points = utils.depcopy(this.points);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return lc;
    }

    @Override
    public int compareTo(Lane lane) {
        if(this.getId() < lane.getId()) return -1;
        else return 1;
    }
}
