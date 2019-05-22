package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.monitor.model.DTO.ReleaseLockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ClassName ReleaseLockListener
 * Author  Lin
 * Date 2019/5/19 19:58
 **/

@Component
public class ReleaseLockListener extends BasicListener {
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
        /**解析releaseLock的消息*/
        ReleaseLockInfo releaseLockInfo = JSONObject.parseObject(body, ReleaseLockInfo.class);

        /**需要lane_id ---> lane name 映射*/
        int cur_id = releaseLockInfo.getCurrent_lane_id();

        car.setLane(String.valueOf(cur_id));

        /**异步存入redis*/
        autoCarInfoService.save(car);

      /*  CarDynamic carDynamic = carDynamicService.query(vin);
        String parkName = carDynamic.getParkName();
        LiveMap liveMap = mapService.getMap(parkName);
        Map<String,List<String>> laneToCarMap  = liveMap.getLaneToCarMap();
        Map<String,String> carMap = liveMap.getCarMap();
        String cur_lane_id = "";
        if(carMap.get(vin) != null && carMap.get(vin) != "") {
            cur_lane_id = carMap.get(vin);
        }
        *//**修改车道级定位记录  *//*
        carMap.put(vin,String.valueOf(cur_id));
        if(!cur_lane_id.equals("")){
            laneToCarMap.get(String.valueOf(cur_lane_id)).remove(vin);
            laneToCarMap.get(String.valueOf(cur_id)).add(vin);
        }
        *//**删除某条LiveLane车辆到达的记录*//*
        List<CarArrivalslnfo> car_time_records = liveMap.getLaneMap().get(String.valueOf(cur_id)).getVehicles();
        car_time_records.remove(car_time_records.size() - 1);

        LiveJunction liveJunction = liveMap.getJunctionMap().get(String.valueOf(cur_id));*/
        /**以cur_id寻找到对应的liveJunction然后借助NodeLock模块发起锁释放的调度*/
/*
        scheduleService.checkJunctionLock(vin,liveJunction,true);
*/

        return new BasicCarResponse(0, new Object());
    }
}
