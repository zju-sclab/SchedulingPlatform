package com.skywilling.cn.livemap.model;

import com.skywilling.cn.common.model.Node;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

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
   private double priority;
   private String type;
   private String zh;
   /**
    * 动态添加车辆的出入信息，记录进入这个路的起点时间和离开这条路终点的时间信息
    */
   private ConcurrentHashMap<String,CarArrivalslnfo> vehicles_time_table;

}
