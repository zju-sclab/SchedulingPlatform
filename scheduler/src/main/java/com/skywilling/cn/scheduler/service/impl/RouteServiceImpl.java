package com.skywilling.cn.scheduler.service.impl;

import com.skywilling.cn.common.model.Coordinate;
import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.StationService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.scheduler.core.RouteLogic;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.model.Trip;
import com.skywilling.cn.scheduler.service.RouteService;
import com.skywilling.cn.scheduler.service.TripService;
import com.skywilling.cn.scheduler.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    MapService mapService;
    @Autowired
    StationService stationService;
    @Autowired
    RouteLogic routeLogic;
    @Autowired
    TripService tripService;

    /**
     * 查询起点到终点的路线Route
     * 输入是站点名
     */
    @Override
    public Route navigate(String parkName, String from, String to) {
        LiveMap map = mapService.getMap(parkName);
        if (map == null) {
            return null;
        }
        Node start = map.getNodeMap().get(from);
        Node end = map.getNodeMap().get(to);
        return route(map, start, end);
    }

    /**
     *  输入输出都是合流点
     */
    private Route route(LiveMap map, Node from, Node to) {
        if (from == null || to == null) {
            return null;
        }
        Route route = new Route();
        route.setParkName(map.getParkName());
        route.setFrom(from);
        route.setTo(to);
        route.setLiveLanes(routeLogic.routePlanning(map, from, to));
        return route;
    }
    /**
     * 查询起点到终点的路线Route
     * 根据中间的LiveStation查询起点合流点，
     * 相当于 做了一个近似的处理，A->B 变成 C->D
     * 现在所有的停车点都是站点，不考虑非站点停车...
     */
    private Route route(LiveMap map, Node from, LiveStation to) {
/*        if (from == null || to == null) {
            return null;
        }
        Route route = new Route();
        route.setParkName(map.getParkName());
        route.setFrom(from);
        route.setTo(to);
        LiveJunction startJunction = map.getLaneMap().get(from.getLocationLane()).getFrom();
        LiveJunction endJunction = map.getLaneMap().get(to.getLocationLane()).getTo();
        route.setLiveLanes(routeLogic.routePlanning(map, startJunction, endJunction));
        return route;*/
        return  null;
    }

    /**
     *  云端定位模块
     *  找到距离车辆的最近的站点位置
     */
    public LiveStation positioningStation(Route route, Coordinate coordinate) {
        LiveMap liveMap = mapService.getMap(route.getParkName());
        if (liveMap == null) return null;

        double minD= Double.MAX_VALUE;
        List<LiveStation> stations = new ArrayList<>(liveMap.getStationMap().values());
        LiveStation nearest = stations.get(0);
        for (int i = 1; i < stations.size(); i++) {
            LiveStation s = stations.get(i);
            double distance = FunctionUtils.distance(s.getX(), s.getY(), coordinate.getX(), coordinate.getY());
            if (distance < minD) {
                minD = distance;
                nearest = s;
            }
        }
        return nearest;
    }


    /**
     * 找到车辆的位置，根据车辆上传的信息
     */
    @Override
    public Route reRoute(AutonomousCarInfo carInfo) {

        //String vin = carInfo.getVin();
        String from = carInfo.getStation();
        Trip oldTrip = tripService.get(carInfo.getTripId());
        Route oldRoute = oldTrip.getRoute();
        String to = oldRoute.getTo().getName();
        String parkName = oldTrip.getParkName();
        if(from != to)
            return this.navigate(parkName, from, to);
        else
            return null;
    }
}
