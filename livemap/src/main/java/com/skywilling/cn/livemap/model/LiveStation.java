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

    //编号
    private int id;
    //对应xml文件的name
    private String name;
    //所在车道
    private String locationLane;
    //已经通过当前节点的车辆的集合
    private LinkedList<String> vehicles;


}
