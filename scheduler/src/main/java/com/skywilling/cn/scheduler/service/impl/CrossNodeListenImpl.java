package com.skywilling.cn.scheduler.service.impl;


import com.esotericsoftware.minlog.Log;
import com.skywilling.cn.command.biz.AutoServiceBiz;
import com.skywilling.cn.connection.service.RequestSender;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.scheduler.core.TripCore;
import com.skywilling.cn.scheduler.service.CrossNodeListen;
import com.skywilling.cn.scheduler.service.NodeLockService;
import com.skywilling.cn.scheduler.service.RouteService;
import com.skywilling.cn.scheduler.service.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CrossNodeListenImpl implements CrossNodeListen {

    Logger log = LoggerFactory.getLogger(CrossNodeListen.class);
    @Autowired
    private AutoCarInfoService autoCarInfoService;
    @Autowired
    MapService mapService;

    @Autowired
    ParkService parkService;
    @Autowired
    NodeLockService nodeLockService;
    @Autowired
    RouteService routeService;
    @Autowired
    CarDynamicService carDynamicService;
    @Autowired
    TripService tripService;
    @Autowired
    TripCore tripCore;

    @Autowired
    AutoServiceBiz autoServiceBiz;
    @Autowired
    RequestSender requestSender;

    @Value("${config.switch-on}")
    private String switchOn;


    @Override
    public void inComingJunction(String vin,String laneId, String junctionName) {
        //开关
        if (!switchOn.equals("true")) {
            return;
        }
        if (nodeLockService.vehicleIsExist(vin, junctionName)) {
            return;
        }
        //long start =  System.currentTimeMillis();
        CompletableFuture<Boolean> acquireRes = nodeLockService.acquire(vin,laneId, junctionName);
        Boolean aBoolean = acquireRes.getNow(false);
        //如果获取锁失败，暂停车端任务
/*        if (!aBoolean) {
            //Trip trip = tripService.get(carInfo.getTripId());
            //tripCore.kill(trip);

            autoServiceBiz.pauseAutonomous(vin);
        }else{
            autoServiceBiz.continueAutonomous(vin);
        }*/
        autoServiceBiz.responseLockAutonomous(vin,aBoolean);
    }

    /**队列中选择最前面的车唤醒，重启自动驾驶任务*/
    @Override
    public void outGoingJunction(String vin, String junctionName) {

        Log.warn("crossNodeListen outgoing: "+vin + " at " + junctionName);
        String next_block_Car = nodeLockService.release(vin, junctionName);
        if (next_block_Car != null){
            AutonomousCarInfo car = autoCarInfoService.get(next_block_Car);
            autoServiceBiz.continueAutonomous(car.getVin());
        }
        autoServiceBiz.responseLockAutonomous(vin,true);

    }
}
