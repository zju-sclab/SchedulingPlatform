package com.skywilling.cn.scheduler.service.impl;


import com.mysql.jdbc.log.LogFactory;
import com.skywilling.cn.livemap.service.LaneService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.scheduler.core.Scheduler;
import com.skywilling.cn.scheduler.service.NodeLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class NodeLockServiceImpl implements NodeLockService {
    public static Logger Log = LoggerFactory.getLogger(NodeLockService.class);
    @Autowired
    CarDynamicService carDynamicService;
    @Autowired
    LaneService laneService;
    @Autowired
    ParkService parkService;

    ConcurrentHashMap<String, Scheduler.NodeLock> nodeLockMap = new ConcurrentHashMap<>();

    @Override
    public Scheduler.NodeLock find(String nodeName) {
        return nodeLockMap.get(nodeName);
    }

    @Override
    public CompletableFuture<Boolean> acquire(String vin, String laneId, String nodeName) {
        Scheduler.NodeLock nodeLock;
        if (!nodeLockMap.containsKey(nodeName)) {
            nodeLock = new Scheduler.NodeLock(nodeName);
            Log.warn("nodelock named: " + nodeName + "has been created!");
            nodeLockMap.put(nodeName, nodeLock);
        }
        nodeLock = nodeLockMap.get(nodeName);
        CarDynamic carDynamic = carDynamicService.query(vin);
        /**car_cur_lane 的 priority*/
        String parkName = parkService.query(carDynamic.getParkId()).getName();
        double priority = laneService.getLane(parkName, laneId).getPriority();
        return nodeLock.acquire(vin, priority);
    }

    @Override
    public String release(String vin, String nodeName) {
        Scheduler.NodeLock nodeLock = nodeLockMap.get(nodeName);
        String result = null;
        if (nodeLock != null) {
            Log.warn("NodeLockService release is execute, step into nodelock release !");
            result = nodeLock.release(vin);
        }
        Log.warn("NodeLockService release not execute!");
        return result;
    }


    @Override
    public boolean vehicleIsExist(String vin, String junctionName) {
        Scheduler.NodeLock nodeLock = nodeLockMap.get(junctionName);
        if (nodeLock != null && nodeLock.hasCar(vin)) {
            return true;
        }
        return false;
    }
}
