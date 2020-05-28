package com.skywilling.cn.scheduler.service;

import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.scheduler.core.Scheduler;

import java.util.concurrent.CompletableFuture;


public interface NodeLockService {

    Scheduler.NodeLock find(String nodeName);

    CompletableFuture<Boolean> acquire(String vin,String laneId, String nodeName);

    String release(String vin, String nodeName);

    boolean vehicleIsExist(String vin, String junctionName);
}
