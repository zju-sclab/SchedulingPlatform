package com.skywilling.cn.livemap.model;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 地图
 */
@Data
public class LiveMap implements Serializable {

    private String parkName;
    private ConcurrentHashMap<String, Node> nodeMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, LiveJunction> junctionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, LiveStation> stationMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, LiveLane> laneMap = new ConcurrentHashMap<>();


}
