package com.skywilling.cn.livemap.model;

import com.skywilling.cn.common.model.Node;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 地图
 */
@Data
public class LiveMap implements Serializable {

    private String parkName;
    //广义Node站点
    private ConcurrentHashMap<String, Node> nodeMap = new ConcurrentHashMap<>();
    //根据name查找Node站点
    private ConcurrentHashMap<String,Node> nameToNodeMap = new ConcurrentHashMap<>();
    //合流点，循环巴士是一个点，物流里面是一条lane
    private ConcurrentHashMap<String, LiveJunction> junctionMap = new ConcurrentHashMap<>();
    //循环巴士固定站点
    private ConcurrentHashMap<String, LiveStation> stationMap = new ConcurrentHashMap<>();
    //路段分为弯道和直线
    private ConcurrentHashMap<String, LiveLane> laneMap = new ConcurrentHashMap<>();

    //记录所有链接到云端的车辆,all car_id
    private Set<String> carsSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    //记录申请锁的车辆
    private ConcurrentHashMap<String,String> carReqLockMap = new ConcurrentHashMap<>();
    //记录车辆所在车道的id, car_id ---> lane_id
    private ConcurrentHashMap<String, String> carMap = new ConcurrentHashMap<>();
    //车道id对应车辆，lane-id ---> car_ids
    private ConcurrentHashMap<String, List<String>> laneToCarMap = new ConcurrentHashMap<>();


}
