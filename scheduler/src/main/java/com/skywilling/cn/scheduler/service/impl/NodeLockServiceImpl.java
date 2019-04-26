package com.skywilling.cn.scheduler.service.impl;


import com.skywilling.cn.livemap.service.LaneService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.scheduler.core.Scheduler;
import com.skywilling.cn.scheduler.service.NodeLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NodeLockServiceImpl implements NodeLockService {

    @Autowired
    CarDynamicService carDynamicService;
    @Autowired
    LaneService laneService;

    ConcurrentHashMap<String, Scheduler.NodeLock> nodeLockMap = new ConcurrentHashMap<>();

    @Override
    public Scheduler.NodeLock find(String nodeName) {
        return nodeLockMap.get(nodeName);
    }

    @Override
    public CompletableFuture<Boolean> acquire(AutonomousCarInfo car, String nodeName) {
        Scheduler.NodeLock nodeLock;
        if (!nodeLockMap.containsKey(nodeName)) {
            nodeLock = new Scheduler.NodeLock(nodeName);
            nodeLockMap.put(nodeName, nodeLock);
        }
        nodeLock = nodeLockMap.get(nodeName);
        CarDynamic carDynamic = carDynamicService.query(car.getVin());
        double priority = laneService.getLane(carDynamic.getParkName(), car.getLane()).getPriority();
        return nodeLock.acquire(car.getVin(), priority);
    }

    @Override
    public String release(String vin, String nodeName) {
        Scheduler.NodeLock nodeLock = nodeLockMap.get(nodeName);
        String result = null;
        if (nodeLock != null) {
            result = nodeLock.release(vin);
        }
        return result;
    }


    @Override
    public boolean vehicleIsExist(String vin, String junctionName) {
        Scheduler.NodeLock nodeLock = nodeLockMap.get(junctionName);
        if (nodeLock != null && nodeLock.getInComingVehicles().contains(vin)) {
            return true;
        }
        return false;
    }
}
