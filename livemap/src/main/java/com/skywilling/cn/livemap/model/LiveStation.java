package com.skywilling.cn.livemap.model;


import com.skywilling.cn.common.model.Node;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;


/**
 * 车站,停车点
 */
@Data
public class LiveStation extends Node implements Serializable {


    //所在车道
    private String locationLane;
    //已经通过当前节点的车辆的集合
    private LinkedList<String> vehicles;


}
