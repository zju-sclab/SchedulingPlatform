package com.skywilling.cn.scheduler.service.impl;

import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.connection.model.CarClient;
import com.skywilling.cn.livemap.model.CarArrivalslnfo;
import com.skywilling.cn.livemap.model.LaneShape;
import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.service.LaneService;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.repository.AutoCarInfoAccessor;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.scheduler.service.CrossNodeListen;
import com.skywilling.cn.scheduler.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ClassName ScheduleServiceImpl
 * Author  Lin
 * Date 2019/5/7 13:38
 **/

public class ScheduleServiceImpl implements ScheduleService {


    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    AutoCarInfoAccessor autoCarInfoAccessor;
    @Autowired
    ClientService clientService;
    @Autowired
    LaneService laneService;
    @Autowired
    MapService mapService;
    @Autowired
    CarDynamicService carDynamicService;
    @Autowired
    CrossNodeListen crossNodeListen;

    /** 调度bean注入完成后在后台持续检测所有车端上传信息通道，检测所有车辆的实时位置，根据规则进行调度规划 */
    @PostConstruct
    @Override
    public void checkAllClient() {

        List<String> vins  = clientService.getAllClients();
        for(String vin : vins){
            AutonomousCarInfo carInfo = autoCarInfoService.get(vin);
            String lane = carInfo.getLane();
            String parkName = carDynamicService.query(vin).getParkName();
            String live_from = carInfo.getFromLane();
            LiveLane liveLane_from = null;
            if(live_from != null)
                liveLane_from = laneService.getLane(parkName,live_from);
            LiveLane liveLane = laneService.getLane(parkName,lane);
            //普通站点或者出弯道进入直线
            if(live_from == null && liveLane_from == null || liveLane_from.getType()==" curve"&& liveLane.getType()=="lane"){
                 checkLaneTimeWindow(vin,liveLane);
            }
            else{
                //获取路口所处的节点
                LiveJunction liveJunction = mapService.getMap(parkName).getJunctionMap().get(liveLane.getTo());
                if(liveLane_from.getType() == "lane" &&liveLane.getType() =="curve")
                    //lane --> curve 表示进入弯道
                    checkJunctionLock(vin, liveJunction, false);
                else
                    //curve --> lane 表示出弯道
                    checkJunctionLock(vin,liveJunction, true);
            }
        }
    }

    /** 一直检测某条路上时间窗口是否当前车辆距离上一辆时间距离小于10s */
    @Override

    public void checkLaneTimeWindow(String vin, LiveLane liveLane) {
         List<CarArrivalslnfo> cars = liveLane.getVehicles();
         AutonomousCarInfo carInfo = autoCarInfoService.get(vin);
         CarArrivalslnfo carArrivalslnfo = cars.get(cars.size()-1);
         //无车
         if(cars == null || cars.isEmpty()){
             //run

         }
         //有车很远  10s以外
         else if(carInfo.getTimestamp()- carArrivalslnfo.getTimestamp() >= 10*1000 ){
             //run
             //do nothing but to add current car to lane
             cars.remove(cars.size()-1);
             CarArrivalslnfo newcar = new CarArrivalslnfo();
             newcar.setTimestamp(carInfo.getTimestamp());
             newcar.setVin(vin);
             cars.add(newcar);
         }
         //有车太近
         else{
             //stop

         }
    }
    /** 一直检测某个路口上的路口锁的获取和释放是否完成 */
    @Override
    public void checkJunctionLock(String vin, LiveJunction liveJunction, boolean isRelease) {
           AutonomousCarInfo carInfo = autoCarInfoService.get(vin);
           if(!isRelease)
               crossNodeListen.inComingJunction(carInfo, liveJunction.getName());
           else
               crossNodeListen.outGoingJunction(carInfo,liveJunction.getName());
    }
}
