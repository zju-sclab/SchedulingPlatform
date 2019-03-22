package com.skywilling.cn.scheduler.service.impl;


import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.model.LiveStation;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.StationService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.scheduler.core.RouteLogic;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    MapService mapService;
    @Autowired
    StationService stationService;
    @Autowired
    RouteLogic routeLogic;
    @Override
    public Route navigate(String parkName, String from, String to) {
        LiveMap map = mapService.getMap(parkName);
        if(map==null){
            return null;
        }
        LiveStation start=stationService.get(map.getParkName(),from);
        LiveStation end=stationService.get(map.getParkName(),to);
        return route(map,start,end);
    }

    private Route route(LiveMap map, LiveStation from, LiveStation to){
        if (from == null || to == null) {
            return null;
        }
        Route route = new Route();
        route.setParkName(map.getParkName());
        route.setFrom(from);
        route.setTo(to);
        LiveJunction startJunction=map.getLaneMap().get(from.getLocationLane()).getFrom();
        LiveJunction endJunction=map.getLaneMap().get(to.getLocationLane()).getTo();
        route.setLiveLanes(routeLogic.routePlanning(map,startJunction, endJunction));
        return route;
    }



    @Override
    public Route reRoute(AutonomousCarInfo carInfo) {

        /**
         * 找到车辆的位置
         */
        return null;
    }
}
