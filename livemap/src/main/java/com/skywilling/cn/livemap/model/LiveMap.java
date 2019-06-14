package com.skywilling.cn.livemap.model;

import com.skywilling.cn.common.model.AutoCarRequest;
import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.livemap.util.CacheManager;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 地图
 */
@Data
public class LiveMap implements Serializable {

    private String parkName;
    //根据id查找Node站点
    private ConcurrentHashMap<String, Node> nodeMap = new ConcurrentHashMap<>();
    //根据name查找Node站点
    private ConcurrentHashMap<String,Node> nameToNodeMap = new ConcurrentHashMap<>();
    //合流点，循环巴士固定站点
    private ConcurrentHashMap<String, LiveJunction> junctionMap = new ConcurrentHashMap<>();
    //普通点，循环巴士固定站点
    private ConcurrentHashMap<String, LiveStation> stationMap = new ConcurrentHashMap<>();
    //路段表，路段根据type分为弯道和直线
    private ConcurrentHashMap<String, LiveLane> laneMap = new ConcurrentHashMap<>();

    //车集合，记录所有链接到云端的车辆
    private Set<String> carsSet = new HashSet<>();
    //车锁表，记录申请锁的车辆
    private ConcurrentHashMap<String,AutoCarRequest> carReqLockMap = new ConcurrentHashMap<>();
    //车路表，记录车辆所在车道的id, car_id ---> lane_id
    private ConcurrentHashMap<String, String> carMap = new ConcurrentHashMap<>();
    //路车表，车道id对应车辆，lane-id ---> car_ids
    private ConcurrentHashMap<String, List<String>> laneToCarMap = new ConcurrentHashMap<>();

    //private static CacheManager cacheManager = new CacheManager();


}
