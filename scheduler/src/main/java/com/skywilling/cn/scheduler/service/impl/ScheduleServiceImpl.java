package com.skywilling.cn.scheduler.service.impl;

import com.skywilling.cn.common.model.AutoCarRequest;
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
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName ScheduleServiceImpl
 * Author  Lin
 * Date 2019/5/7 13:38
 **/

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static final  Logger LOG = LoggerFactory.getLogger(ScheduleServiceImpl.class);

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

    @Scheduled(fixedRate = 1000)
    @Override
    public void checkAllClient() {
        List<LiveMap> maps = mapService.getAllMaps();
        //LOG.info("schedule maps size: " + maps.size());
        for(LiveMap livemap : maps) {
            //LOG.info("schedule map name: "+livemap.getParkName());
            ConcurrentHashMap<String,AutoCarRequest> car_req_lock = livemap.getCarReqLockMap();
            LOG.info("schedule car_req_lock: "+car_req_lock.keySet().size()+" request at: "+livemap.getParkName());
            for (String vinreq : car_req_lock.keySet()) {
                AutoCarRequest carRequest = car_req_lock.get(vinreq);
                String vin = carRequest.getVin();
                AutonomousCarInfo carInfo = autoCarInfoService.get(vin);
                if(carInfo == null){
                    LOG.warn("car "  + vin + " is not connected, can`t schedule!");
                    continue;
                }

                boolean is_request = carRequest.isRequestFlag();
                String cross_id = carRequest.getCross_id();
                String lane_id = carRequest.getLane_id();

                //release req
                if(!is_request){
                    LOG.info("schedule car_release_lock_req: " + vin + " at: " + cross_id);
                    LiveJunction liveJunction = livemap.getJunctionMap().get(cross_id);
                    crossNodeListen.outGoingJunction(vin,liveJunction.getName());
                    car_req_lock.remove(vinreq);
                }
                else
               {//request req
                    LOG.info("schedule car_request_lock_req: " + carInfo.getVin() + " at: " + cross_id);
                    LiveJunction liveJunction = livemap.getJunctionMap().get(cross_id);
                    crossNodeListen.inComingJunction(vin,lane_id, liveJunction.getName());
                    car_req_lock.remove(vinreq);
                }

            }
            livemap.setCarReqLockMap(car_req_lock);
            mapService.addMap(livemap);
        }
    }

    /** 一直检测某个路口上的路口锁的获取和释放是否完成 */
    @Override
    public void checkJunctionLock(String vin, String laneId, LiveJunction liveJunction, boolean isRelease) {
           if(!isRelease)
               crossNodeListen.inComingJunction(vin,laneId, liveJunction.getName());
           else
               crossNodeListen.outGoingJunction(vin,liveJunction.getName());
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
}
