package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.AutoCarRequest;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.livemap.model.CarArrivalslnfo;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.monitor.model.DTO.RequestLockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName RequsetLockListener
 * Author  Lin
 * Date 2019/5/19 16:08
 **/

@Component
public class RequsetLockListener extends  BasicListener {

    private static final Logger LOG = LoggerFactory.getLogger(RequsetLockListener.class);
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    CarDynamicService carDynamicService;
    @Autowired
    MapService mapService;
    @Autowired
    ParkService parkService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.REQUEST_LOCK.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, String body) {
        AutonomousCarInfo car = autoCarInfoService.getOrCreate(vin);
        /**判断是否登陆的依据是redis是否存有该车信息,登陆逻辑会添加车辆vin到redis*/
        if (car == null) {
            return null;
        }
        LOG.info("进入锁请求监听器内部：");
        /**解析requestLock的消息*/
        RequestLockInfo requestLockInfo  = JSONObject.parseObject(body, RequestLockInfo.class);

        int parkId = carDynamicService.query(car.getVin()).getParkId();
        String parkName = parkService.query(parkId).getName();

        AutoCarRequest autoCarRequest = new AutoCarRequest();
        autoCarRequest.setLock(true);
        autoCarRequest.setVin(vin);

        LiveMap liveMap = mapService.getMap(parkName);
        Map<String,List<String>> laneToCarMap  = liveMap.getLaneToCarMap();
        Map<String,String> carMap = liveMap.getCarMap();

        String last_lane_id = null;
        /** 如果存在vin的车道定位记录,删除旧的,添加新的 */
        if(carMap.get(vin) != null )
            last_lane_id = carMap.get(vin);

        /**修改车道定位记录  */
        if(last_lane_id != null){
            laneToCarMap.get(last_lane_id).remove(vin);
        }
        /**修改某条LiveLane车辆到达的记录*/
      /*  List<CarArrivalslnfo> car_time_records = liveMap.getLaneMap().get(next_id).getVehicles();
        CarArrivalslnfo arrivalslnfo = new CarArrivalslnfo();
        arrivalslnfo.setVin(vin);
        arrivalslnfo.setTimestamp(System.currentTimeMillis());
        car_time_records.add(arrivalslnfo);*/

        String cur_id = requestLockInfo.getCurrent_id();
        String next_id = requestLockInfo.getNext_id();
        if(cur_id.startsWith("cross")){
            String[] curIds = cur_id.split("_");
            if(curIds[1].equals("8888")){
                if(mapService.getMap(parkName).getCarMap() != null)
                    mapService.getMap(parkName).getCarMap().put(vin,next_id);
            }
        }
        else if(cur_id.startsWith("lane")){
            String[] nextIds = next_id.split("_");
            if(nextIds[1].equals("9999")){
                if(liveMap.getCarMap()!=null)
                    liveMap.getCarMap().put(vin,cur_id);
            }
            else{
                if(mapService.getMap(parkName).getCarMap()!=null)
                    mapService.getMap(parkName).getCarMap().put(vin,cur_id);
                autoCarRequest.setLane_id(cur_id);
                autoCarRequest.setCross_id(next_id);
                liveMap.getCarReqLockMap().put(vin+"request"+System.currentTimeMillis(),autoCarRequest);
                if(liveMap.getLaneToCarMap().get((next_id)) == null ) {
                    liveMap.getLaneToCarMap().put(next_id, new ArrayList<>());
                }
                liveMap.getLaneToCarMap().get(next_id).add(vin);
            }
        }
        LOG.warn("request_lock listener record carReqMap size: [{}] " , liveMap.getCarReqLockMap().size());
        mapService.addMap(liveMap);
        /**异步存入redis*/
        autoCarInfoService.save(car);

        return new BasicCarResponse(0, new Object());
    }
}
