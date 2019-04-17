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
   private double length = 0.0;
   private int time = 0;
   private String zh;
   private double v;
   private LiveJunction from;
   private LiveJunction to;

   /**
    * 动态
    */
   private LinkedList<String> vehicles;
   private double weight;
   private int priority;

}
