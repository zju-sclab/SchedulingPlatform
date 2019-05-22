package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.monitor.model.DTO.RequestLockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ClassName RequsetLockListener
 * Author  Lin
 * Date 2019/5/19 16:08
 **/

@Component
public class RequsetLockListener extends  BasicListener {
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    CarDynamicService carDynamicService;


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
        /**解析requestLock的消息*/
        RequestLockInfo requestLockInfo  = JSONObject.parseObject(body, RequestLockInfo.class);

        /**需要lane_id ---> lane name 映射*/
        int cur_id = requestLockInfo.getCurrent_lane_id();
        int next_id = requestLockInfo.getTarget_cross_id();

        car.setFromLane(String.valueOf(cur_id));
        car.setLane(String.valueOf(next_id));

        /**异步存入redis*/
        autoCarInfoService.save(car);

      /*  CarDynamic carDynamic = carDynamicService.query(vin);
        String parkName = carDynamic.getParkName();
        LiveMap liveMap = mapService.getMap(parkName);
        Map<String,List<String>> laneToCarMap  = liveMap.getLaneToCarMap();
        Map<String,String> carMap = liveMap.getCarMap();
        String cur_lane_id = "";
        *//** 如果存在vin的车道定位记录,删除旧的,添加新的 *//*
        if(carMap.get(vin) != null && carMap.get(vin) != "")
            cur_lane_id = carMap.get(vin);

        *//**修改车道级定位记录  *//*
        if(cur_lane_id != ""){
            laneToCarMap.get(cur_lane_id).remove(vin);
            laneToCarMap.get(cur_id).add(vin);
        }
        *//**修改某条LiveLane车辆到达的记录*//*
        List<CarArrivalslnfo> car_time_records = liveMap.getLaneMap().get(String.valueOf(next_id)).getVehicles();
        CarArrivalslnfo arrivalslnfo = new CarArrivalslnfo();
        arrivalslnfo.setVin(vin);
        arrivalslnfo.setTimestamp(System.currentTimeMillis());
        car_time_records.add(arrivalslnfo);

        LiveJunction liveJunction = liveMap.getJunctionMap().get(String.valueOf(next_id));
        *//**以cur_id寻找到对应的liveJunction然后借助NodeLock模块发起锁申请的调度*//*
        scheduleService.checkJunctionLock(vin,liveJunction,false);*/

        return new BasicCarResponse(0, new Object());
    }
}
