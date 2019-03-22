package com.skywilling.cn.scheduler.service.impl;


import com.skywilling.cn.common.exception.IllegalTaskException;
import com.skywilling.cn.common.exception.park.NoAvailableActionFoundException;
import com.skywilling.cn.livemap.service.MapService;
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
    NodeLockService nodeLockService;
    @Autowired
    RouteService routeService;
    @Autowired
    CarDynamicService carDynamicService;
    @Autowired
    TripService tripService;
    @Autowired
    TripCore tripCore;

    @Value("config.switch-on")
    private String switchOn;


    @Override
    public void inComingJunction(AutonomousCarInfo carInfo, String junctionName) {
        //判断开关
        if (!switchOn.equals(true)){
            return;
        }

        if (nodeLockService.vehicleIsExist(carInfo.getVin(), junctionName)) {
            return;
        }

        CompletableFuture<Boolean> acquire = nodeLockService.acquire(carInfo, junctionName);
        Boolean aBoolean = acquire.getNow(false);
        if (!aBoolean) {

            Trip trip = tripService.get(carInfo.getTripId());
            tripCore.kill(trip);
        }
    }


    @Override
    public void outGoingJunction(AutonomousCarInfo carInfo, String junctionName) {

        if (!switchOn.equals(true)){
            return;
        }

        //重新开始车辆任务
        String nextCar=nodeLockService.release(carInfo.getVin(),junctionName);
        if (nextCar == null) return;
        AutonomousCarInfo car = autoCarInfoService.get(nextCar);
        Route route = routeService.reRoute(carInfo);
        Trip trip=tripService.updateRoute(carInfo, route);
        try {
            tripCore.submitTrip(trip);
        } catch (IllegalTaskException e) {
            e.printStackTrace();
        } catch (NoAvailableActionFoundException e) {
            e.printStackTrace();
        }
    }

}
