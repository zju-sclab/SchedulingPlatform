package com.skywilling.cn.scheduler.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.skywilling.cn.common.exception.CarNotAliveException;
import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.common.exception.IllegalRideException;
import com.skywilling.cn.common.exception.IllegalTaskException;
import com.skywilling.cn.common.exception.NoAvailableActionFoundException;
import com.skywilling.cn.common.model.*;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.StationService;
import com.skywilling.cn.manager.car.enumeration.CarState;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.CarInfoService;
import com.skywilling.cn.scheduler.common.TripStatus;
import com.skywilling.cn.scheduler.core.TripCore;
import com.skywilling.cn.scheduler.core.trajectoryalgorithm.GlobalTrajPlanner;
import com.skywilling.cn.scheduler.model.RideStatus;
import com.skywilling.cn.scheduler.model.Route;
import com.skywilling.cn.scheduler.model.StaticStation;
import com.skywilling.cn.scheduler.model.Trip;
import com.skywilling.cn.scheduler.repository.TripAccessor;
import com.skywilling.cn.scheduler.service.RouteService;
import com.skywilling.cn.scheduler.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TripServiceImpl implements TripService {
    @Autowired
    TripAccessor tripAccessor;
    @Autowired
    TripCore tripCore;
    @Autowired
    CarInfoService carInfoService;
    @Autowired
    RouteService routeService;
    @Autowired
    StationService stationService;
    @Autowired
    MapService mapService;

    @Autowired
    GlobalTrajPlanner trjPlanService;

    @Override
    public boolean stopTrip(String tripId) {
        Trip trip = this.get(tripId);
        if (trip == null || trip.getStatus() > TripStatus.FINISHED.getCode()) {
            return true;
        }
        tripCore.kill(trip);
        return false;
    }

    /**
     * 自动驾驶订单接口的核心模块
     * 1.产生任务并设置任务参数
     * 2.规划路径
     */
    @Override
    public String submitTrip(String vin, String parkName, String from, String goal, double velocity, double acceleration)
                                   throws CarNotExistsException, CarNotAliveException, IllegalRideException {

       AutonomousCarInfo car = carInfoService.getAutoCarInfo(vin);
        /**判断该车是否链接上云平台 */
        if (car == null) {
            throw new CarNotExistsException(vin);
        }
        /**判断该车是否链接丢失 */
        if (car.getState() == CarState.LOST.getState()) {
            throw new CarNotAliveException(vin);
        }
        /**判断该是否发起不合理订单 */
        if (from == goal)
            throw new IllegalRideException();
        /**内存初始化镜像地图*/
        LiveMap liveMap = mapService.getMap(parkName);
        if(liveMap == null )
            mapService.createMapByLidarMap(parkName);
        /** 查找全局路径返回Route*/
        Route route = routeService.navigate(parkName, from, goal);
        /** 设置速度和加速度*/
        for (LiveLane lane : route.getLiveLanes()) {
                lane.setV(velocity);
        }
        /** 生成自动驾驶任务序列*/
        String tripId = tripCore.generateTripId(vin);
        Trip trip = new Trip(vin, tripId, route);
        try {
            /**提交自动任务序列*/
            tripCore.submitTrip(trip);
            /**成功则返回TripId*/
            return trip.getId();
        } catch (IllegalTaskException | NoAvailableActionFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 自动驾驶订单接口的核心模块
     * 1.产生任务并设置任务参数
     * 2.规划路径
     */
    @Override
    public Trip submitTrjTrip(String vin,String parkName, String goal) throws CarNotExistsException, CarNotAliveException{
        AutonomousCarInfo car = carInfoService.getAutoCarInfo(vin);
        /**判断该车是否链接上云平台 */
        if (car == null) {
            throw new CarNotExistsException(vin);
        }
        /**判断该车是否链接丢失 */
        if (car.getState() == CarState.LOST.getState()) {
            throw new CarNotAliveException(vin);
        }
        /**起点由心跳信息提供*/
        StaticStation outset = new StaticStation(car.getPose());
        //用于测试 出发点确定
//        Node outset_node = mapService.getNode("caolou",parkName);
//        StaticStation outset = createStaticPointByNode(outset_node);
        Node des_node = mapService.getNode(goal,parkName);
        StaticStation destination = createStaticPointByNode(des_node);
        /** 调用全局规划接口计算得到三元数组结果*/
        Triple<List<String>, List<Double>, List<RoutePoint>> res = trjPlanService.createTrajectory(outset,destination);
        List<RoutePoint> routePoints = res.third;
        /**全局规划结果包装成任务*/
        Route route = new Route();
        Node from_node = new Node();
        from_node.setName("source");
        from_node.setX(car.getPose().getPosition().getX());
        from_node.setY(car.getPose().getPosition().getY());
        route.setVin(vin);
        route.setParkName(parkName);
        route.setFrom(from_node);
        route.setTo(des_node);
        route.setLanes(res.first);
        route.setTimes(res.second);
        String tripId = tripCore.generateTripId(vin);
        Trip trip = new Trip(vin, tripId,parkName,route,res.first,res.third);
        try {
            /**提交自动任务序列*/
            tripCore.submitTrjTrip(trip,routePoints);
            /**成功则返回TripId*/
            return trip;
        } catch (IllegalTaskException | NoAvailableActionFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据全局路径重新规划自动驾驶任务序列
     * start为当前停止点的start序列号
     * 废弃当前接口, 直接暂停任务即可
     */
    @Override
    public Trip updateRoute(AutonomousCarInfo carInfo, Route route) {
        Trip trip = this.get(carInfo.getTripId());
        List<LiveLane> old = trip.getRoute().getLiveLanes();
        List<LiveLane> newLanes = new ArrayList<>();
        int i = 0;
        for (LiveLane lane : old) {
            if (!StringUtils.equals(lane.getName(), carInfo.getLane())) {
                newLanes.add(lane);
                i++;
            } else {
                trip.setStart(i);
                newLanes.addAll(route.getLiveLanes());
                trip.getRoute().setLiveLanes(newLanes);
                trip.setEnd(trip.getRoute().getLiveLanes().size());
                break;
            }
        }
        tripAccessor.save(trip);
        return trip;
    }

    /**根据TripId查询Trip的信息*/
    @Override
    public Trip get(String rideId) {
        return tripAccessor.find(rideId);
    }

    /** 根据vin查询最后一个Trip信息*/
    @Override
    public Trip getLiveTripBy(String vin) {
        Trip lasted = tripAccessor.findLastedBy(vin);
        if (lasted != null && lasted.getStatus() < RideStatus.FINISHED.getCode()) {
            return lasted;
        }
        return null;
    }

    /** 查询最后所有Trip信息*/
    @Override
    public List<Trip> query(int page, int size) {
        return tripAccessor.query(page, size);
    }

    /** 根据vin所有的Trip信息*/
    @Override
    public List<Trip> queryBy(String vin, int page, int size) {
        return tripAccessor.queryBy(vin, page, size);
    }

    /** 根据vin和开始时间,结束时间查询所有的Trip信息*/
    @Override
    public List<Trip> queryBy(String vin, Date start, Date end, int page, int size) {
        return tripAccessor.queryBy(vin, start, end, page, size);
    }

    /**Node转为StaticPoint全局规划*/
    @Override
    public StaticStation createStaticPointByNode(Node node) {
        StaticStation des_static_station = new StaticStation(node.getX(),node.getY(),0,0,0,0,0);
        return des_static_station;
    }
   /**批量创建Plan**/
    @Override
    public List<Plan> createPlanByOrders(Order[] orders) {
        List<Plan> plans = new ArrayList<>();
        for(int i = 0; i < orders.length; i++){
            plans.add(createPlanByOrder(orders[i]));
        }
        return plans;
    }
   /**创建Plan**/
    @Override
    public Plan createPlanByOrder(Order order) {
        StaticStation cur = createStaticPointByNode(mapService.getNode(order.getOutset(),order.getParkName()));
        StaticStation next = createStaticPointByNode(mapService.getNode(order.getDestination(),order.getParkName()));
        Triple<List<String>,List<Double>,List<RoutePoint>> res = trjPlanService.createTrajectory(cur,next);
        Plan plan = new Plan(order, res.second, res.first);
        return plan;
    }
}
