package com.skywilling.cn.livemap.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
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
    //car_id ---> lane_id
    private ConcurrentHashMap<String, String> carMap = new ConcurrentHashMap<>();
    //lane-id ---> car_ids
    private ConcurrentHashMap<String, List<String>> laneToCarMap = new ConcurrentHashMap<>();


}
