package com.skywilling.cn.livemap.model;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 车道
 */
@Data
public class LiveLane implements Serializable {
   private static final long serialVersionUID = -1231234234L;
   private String name;
   private int id;
   private double length = 0.0;
   private double v;
   private Node from;
   private Node to;
   private int priority;
   private String type;   //表示lane,curve
   private String zh;   //中文名
   /**
    * 动态添加车辆的出入信息，记录进入这个路的起点时间和离开这条路终点的时间信息
    */
   private LinkedList<CarArrivalslnfo> vehicles;


}
