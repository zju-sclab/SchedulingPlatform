package com.skywilling.cn.scheduler.service;

import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.scheduler.model.NodeLock;

import java.util.concurrent.CompletableFuture;


public interface NodeLockService {

    NodeLock find(String nodeName);

    CompletableFuture<Boolean> acquire(AutonomousCarInfo car, String nodeName);

    String release(String vin, String nodeName);

    boolean vehicleIsExist(String vin, String junctionName);
}
