package com.skywilling.cn.scheduler.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.exception.IllegalTaskException;
import com.skywilling.cn.common.exception.park.NoAvailableActionFoundException;
import com.skywilling.cn.connection.model.Packet;
import com.skywilling.cn.connection.service.RequestSender;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.scheduler.core.TripCore;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.model.Trip;
import com.skywilling.cn.scheduler.service.CrossNodeListen;
import com.skywilling.cn.scheduler.service.NodeLockService;
import com.skywilling.cn.scheduler.service.RouteService;
import com.skywilling.cn.scheduler.service.TripService;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CrossNodeListenImpl implements CrossNodeListen {


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
    RequestSender requestSender;

    @Value("${config.switch-on}")
    private String switchOn;


    @Override
    public void inComingJunction(AutonomousCarInfo carInfo, String junctionName) {
        //判断开关
        if (!switchOn.equals("true")) {
            return;
        }
        if (nodeLockService.vehicleIsExist(carInfo.getVin(), junctionName)) {
            return;
        }
        long start =  System.currentTimeMillis();
        CompletableFuture<Boolean> acquire = nodeLockService.acquire(carInfo, junctionName);
        Boolean aBoolean = acquire.getNow(false);
        //如果获取锁失败，暂停车端任务
        if (!aBoolean) {

//            Trip trip = tripService.get(carInfo.getTripId());
//            tripCore.kill(trip);
            /** 暂停当前任务 */
            String vin = carInfo.getVin();
            requestSender.sendRequest(vin, TypeField.PAUSE_AUTONOMOUS, new JSONObject());
        }


    }

    /**队列中选择最前面的车唤醒，重启自动驾驶任务*/
    @Override
    public void outGoingJunction(AutonomousCarInfo carInfo, String junctionName) {

        if (!switchOn.equals(true)) {
            return;
        }
        String nextCar = nodeLockService.release(carInfo.getVin(), junctionName);
        if (nextCar == null) return;
        AutonomousCarInfo car = autoCarInfoService.get(nextCar);
        String vin = car.getVin();
        //给车端发送重新启动信号
        requestSender.sendRequest(vin,TypeField.RESTART_AUTONOMOUS, new JSONObject());
    }
    //todo
    @Override
    public void OnArrivingStation(AutonomousCarInfo carInfo, String statonName) {

        //20m
    }

}
