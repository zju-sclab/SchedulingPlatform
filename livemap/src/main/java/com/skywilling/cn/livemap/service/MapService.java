package com.skywilling.cn.livemap.service;

import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.model.LiveStation;

import java.util.List;


public interface MapService {
    LiveMap getMap(String parkName);
    void addMap(LiveMap map);
    List<LiveMap> getAllMaps();
    LiveMap createMapByLidarMap(String parkName);
    LiveMap createMapByCycleBus(String parkName);
    void upDateReqLockMap();
    Node getNode(String nodeName,String parkName);
}
