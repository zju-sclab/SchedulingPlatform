package com.skywilling.cn.scheduler.service.impl;

import com.skywilling.cn.connection.infrastructure.client.ClientService;
import com.skywilling.cn.livemap.model.CarArrivalslnfo;
import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.service.LaneService;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.repository.AutoCarInfoAccessor;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.scheduler.service.CrossNodeListen;
import com.skywilling.cn.scheduler.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName ScheduleServiceImpl
 * Author  Lin
 * Date 2019/5/7 13:38
 **/

@Service
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
    @Scheduled(fixedRate = 100)
    @Override
    public void checkAllClient() {
        List<LiveMap> maps = mapService.getAllMaps();
        //SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        for(LiveMap livemap : maps) {
            //System.out.println("schedule time at : "+sdf.format(System.currentTimeMillis()));
            //System.out.println("schedule map name: "+livemap.getParkName());
            ConcurrentHashMap<String,String> car_req_lock = livemap.getCarReqLockMap();
            //System.out.println("schedule car-req_lock: "+car_req_lock.keySet().size()+"req");
            for (String vin : car_req_lock.keySet()) {

                AutonomousCarInfo carInfo = autoCarInfoService.get(vin);
                String cross_lane = carInfo.getLane();
                String from_lane = carInfo.getFromLane();

                if(car_req_lock.get(vin).equals("release")){
                    LiveJunction liveJunction = livemap.getJunctionMap().get(from_lane);
                    checkJunctionLock(vin, liveJunction, true);
                    car_req_lock.remove(vin);
                }
                else if(car_req_lock.get(vin).equals("request"))
               {
                    //获取路口所处的节点
                    LiveJunction liveJunction = livemap.getJunctionMap().get(cross_lane);
                    checkJunctionLock(vin, liveJunction, false);
                    car_req_lock.remove(vin);
                }

            }
        }
    }

    /** 一直检测某条路上时间窗口是否当前车辆距离上一辆时间距离小于10s */
    @Override
    public void checkLaneTimeWindow(String vin, LiveLane liveLane) {
         List<CarArrivalslnfo> cars = new ArrayList<>();
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
