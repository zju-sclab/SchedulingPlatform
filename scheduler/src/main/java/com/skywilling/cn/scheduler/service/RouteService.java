package com.skywilling.cn.scheduler.service;


import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.scheduler.model.Route;

public interface RouteService {

    Route navigate(String parkName, String from, String end);

    Route reRoute(AutonomousCarInfo carInfo);

}
