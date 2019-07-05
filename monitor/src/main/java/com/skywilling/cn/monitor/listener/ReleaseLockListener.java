package com.skywilling.cn.monitor.listener;

import com.alibaba.fastjson.JSONObject;
import com.skywilling.cn.common.enums.TypeField;
import com.skywilling.cn.common.model.AutoCarRequest;
import com.skywilling.cn.common.model.BasicCarResponse;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.monitor.model.DTO.ReleaseLockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClassName ReleaseLockListener
 * Author  Lin
 * Date 2019/5/19 19:58
 **/

@Component
public class ReleaseLockListener extends BasicListener {
    private static final Logger LOG = LoggerFactory.getLogger(RequsetLockListener.class);
    @Autowired
    ListenerMap listenerMap;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    CarDynamicService carDynamicService;
    @Autowired
    ParkService parkService;
    @Autowired
    MapService mapService;

    @Override
    @PostConstruct
    public void init() {
        listenerMap.addListener(TypeField.RELEASE_LOCK.getDesc(), this);
    }

    @Override
    public BasicCarResponse process(String vin, String body) {
        AutonomousCarInfo car = autoCarInfoService.getOrCreate(vin);
        /**判断是否登陆的依据是redis是否存有该车信息,登陆逻辑会添加车辆vin到redis*/
        if (car == null) {
            return null;
        }
        LOG.info("进入锁释放监听器内部 " );
        /**解析releaseLock的消息*/
        ReleaseLockInfo releaseLockInfo = JSONObject.parseObject(body, ReleaseLockInfo.class);
        String cur_id = releaseLockInfo.getCurrent_id();
        String next_id = releaseLockInfo.getNext_id();

        int parkId = carDynamicService.query(car.getVin()).getParkId();
        String parkName = parkService.query(parkId).getName();

        AutoCarRequest autoCarRequest = new AutoCarRequest();
        autoCarRequest.setLock(false);//release req
        autoCarRequest.setLane_id(next_id);
        autoCarRequest.setCross_id(cur_id);
        autoCarRequest.setVin(vin);

        LiveMap liveMap = mapService.getMap(parkName);

        String last_lane_id = null;
        /** 如果存在vin的车道定位记录,删除旧的,添加新的 */
        if(liveMap.getCarMap().get(vin) != null )
            last_lane_id = liveMap.getCarMap().get(vin);

        /**修改车道定位记录  */
        if(last_lane_id != null){
            if(liveMap.getLaneToCarMap().get(last_lane_id) != null)
                liveMap.getLaneToCarMap().get(last_lane_id).remove(vin);
        }
        /**释放当前cur_id标识的合流点并更新位置为当前cross_xx*/
        liveMap.getCarMap().put(vin,cur_id);

        if(liveMap.getLaneToCarMap().get((cur_id)) == null ) {
            liveMap.getLaneToCarMap().put(cur_id, new ArrayList<>());
        }
        liveMap.getLaneToCarMap().get(cur_id).add(vin);

        liveMap.getCarReqLockMap().put(vin+"release"+System.currentTimeMillis(),autoCarRequest);
        LOG.info("release lock listener record carReqMap size: [{}]", liveMap.getCarReqLockMap().size());
        mapService.addMap(liveMap);
        /**异步存入redis*/
        autoCarInfoService.save(car);

        return new BasicCarResponse(0, new Object());
    }
}
