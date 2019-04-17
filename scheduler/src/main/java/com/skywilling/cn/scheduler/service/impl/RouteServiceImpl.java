package com.skywilling.cn.scheduler.service.impl;

import com.skywilling.cn.common.model.Coordinate;
import com.skywilling.cn.common.utils.GPSUtils;
import com.skywilling.cn.livemap.db.mapper.ParkMapper;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.StationService;
import com.skywilling.cn.livemap.service.impl.ParkServiceImpl;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.scheduler.core.RouteLogic;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.model.Trip;
import com.skywilling.cn.scheduler.service.RouteService;
import com.skywilling.cn.scheduler.service.TripService;
import com.skywilling.cn.scheduler.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        LiveStation start = stationService.getStation(map.getParkName(), from);
        LiveStation end = stationService.getStation(map.getParkName(), to);
        return route(map, start, end);
    }

    /**
     * 查询起点到终点的路线Route
     * 输入转化为了LiveStation
     */
    private Route route(LiveMap map, LiveStation from, LiveStation to) {
        if (from == null || to == null) {
            return null;
        }
        Route route = new Route();
        route.setParkName(map.getParkName());
        route.setFrom(from);
        route.setTo(to);
        LiveJunction startJunction = map.getLaneMap().get(from.getLocationLane()).getFrom();
        LiveJunction endJunction = map.getLaneMap().get(to.getLocationLane()).getTo();
        route.setLiveLanes(routeLogic.routePlanning(map, startJunction, endJunction));
        return route;
    }

    /**
     *  根据车辆vin查找所在站点位置
     */

    public LiveStation getStation(String parkName, String vin){
        LiveMap map = mapService.getMap(parkName);
        List<LiveStation> stationList = new ArrayList<>(map.getStationMap().values());
        for(LiveStation liveStation : stationList){
            if(liveStation.getVehicles().contains(vin))
                return liveStation;
        }
        return null;
    }
    /**
     *  根据车辆vin查找所在车道位置
     */
    public LiveLane getLane(String parkName, String vin){
        List<LiveLane> lanes = new ArrayList<>(mapService.getMap(parkName).getLaneMap().values());
        for(LiveLane liveLane : lanes){
            if(liveLane.getVehicles().contains(vin)){
                return liveLane;
            }
        }
        return null;
    }
    /**
     *  根据车辆vin查找所在路口位置
     */
    public LiveJunction getJunction(String parkName, String vin){
        List<LiveJunction> junctions = new ArrayList<>(mapService.getMap(parkName).getJunctionMap().values());
        for(LiveJunction liveJunction : junctions){
            if(liveJunction.getInComingVehicles().contains(vin))
                return liveJunction;
    }
        return null;
    }

    /**
     *  云端定位模块
     *  找到距离车辆的最近的站点位置
     */
    public LiveStation positioningStation(Route route, Coordinate coordinate) {
        //ShapeContainer shapeContainer = shapeContainerService.query(route.getParkName());
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

        String vin = carInfo.getVin();
        String from = carInfo.getStation();
        Trip oldTrip = tripService.get(carInfo.getTripId());
        Route oldRoute = oldTrip.getRoute();
        String to = oldRoute.getTo().toStation().getName();
        String parkName = oldTrip.getParkName();
        if(from != to)
            return this.navigate(parkName, from, to);
        else
            return null;
    }
}
