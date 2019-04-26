package com.skywilling.cn.livemap.model;


import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 车站,停车点
 */
@Data
public class LiveStation extends Node implements Serializable {

    private String locationLane;
    private String name;
    private int    id;
    private LinkedList<String> vehicles;
    private double x;
    private double y;

}
