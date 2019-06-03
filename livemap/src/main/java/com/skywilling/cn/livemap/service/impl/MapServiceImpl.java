package com.skywilling.cn.livemap.service.impl;


import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.livemap.core.StaticMapAndShapeFactory;
import com.skywilling.cn.livemap.core.StaticMapFactory;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.ShapeService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.repository.impl.AutoCarInfoGeoAccessorImpl;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MapServiceImpl implements MapService {
    private static final Logger LOG = LoggerFactory.getLogger(MapServiceImpl.class);
    private static final String PREFIX = "map_";
    private ConcurrentHashMap<String, LiveMap> maps = new ConcurrentHashMap<>();
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private ParkService parkService;
    @Autowired
    private ShapeService shapeService;
    @Autowired
    StaticMapFactory staticMapFactory;
    @Autowired
    StaticMapAndShapeFactory staticMapAndShapeFactory;
    @Autowired
    AutoCarInfoService autoCarInfoService;
    @Autowired
    AutoCarInfoGeoAccessorImpl autoCarInfoGeoAccessor;
    @Autowired
    CarDynamicService carDynamicService;

    //@PostConstruct
    public void init(){
        String parkName = "yuquanxiaoqu3";
        if(!maps.contains(parkName))
        {
            LOG.info("Static Map init to create livemap named: " + parkName);
            LiveMap liveMap = createMapByLidarMap(parkName);
            addMap(liveMap);
        }
        else
           LOG.info("maps size : " + maps.size() + " contains: " + parkName);
    }
   /** 获取所有动态地图 */
   @Override
   public  List<LiveMap> getAllMaps(){
       List<LiveMap> res = new ArrayList<>();
       for(String key : maps.keySet()){
           res.add(maps.get(key));
       }
       return res;
   }
    @Override
    public LiveMap getMap(String parkName) {
        if (!maps.containsKey(parkName)) {
            LOG.info("creat live map : " + parkName + " in memory. ");
            LiveMap liveMap = createMapByLidarMap(parkName);
            maps.put(parkName,liveMap);
        }
        return maps.get(parkName);
    }
    /**构建普通地图*/
    @Override
    public LiveMap createMapByLidarMap(String parkName) {
       Park park = parkService.queryByName(parkName);
       LiveMap liveMap = null;
       if (park != null && park.getShapeFileUrl() != null && park.getMapFileUrl() != null){
           liveMap = staticMapAndShapeFactory.create(parkName,park.getMapFileUrl(),park.getShapeFileUrl());
       }
       else{
           LOG.warn("no map or shape file to create livemap named: " + parkName);
       }
       return liveMap;
    }
    /**构建循环巴士地图*/
    @Override
    public LiveMap createMapByCycleBus(String parkName){
        Park park = parkService.queryByName(parkName);
        if (park != null&& park.getMapFileUrl() != null && park.getShapeFileUrl() != null) {
            //读取本地文件
            LiveMap liveMap = staticMapFactory.create(parkName, park.getMapFileUrl());
            addMap(liveMap);
            shapeService.create(parkName);
            maps.put(liveMap.getParkName(), liveMap);
            return liveMap;
        }
        return null;
    }
    @Override
    public void addMap(LiveMap map) {
        maps.put(map.getParkName(), map);
    }

    /**
     * 每1s钟向redis刷新数据
     */
    @Scheduled(fixedRate = 100000)
    private void syncFile() {
        for (String name : maps.keySet()) {
            redisDao.save(PREFIX + name, maps.get(name));
        }
    }

    /**
     * 每0.1s钟从redis刷新内存数据库的数据到内存的Map
     */
    //@Scheduled(fixedRate = 500)
    @Override
    public void upDateReqLockMap(){

            /**从mongoDb拿到所有车俩的实时数据 */

 /*         List<AutonomousCarInfo> autonomousCarInfoList = autoCarInfoGeoAccessor.getAll();
            LOG.info("update carInfo list size : " + autonomousCarInfoList.size());
            for(AutonomousCarInfo car : autonomousCarInfoList){
                String vin = car.getVin();
                CarDynamic carDynamic = carDynamicService.query(vin);
                if(carDynamic == null) {
                    LOG.warn("no such car: " + vin );
                    continue;
                }
                //todo:carDynamic加入parkName信息
                int parkId = carDynamic.getParkId();
                String parkName = parkService.query(parkId).getName();
                LiveMap map = getMap(parkName);

                *//**从redis拿到实时的数据信息*//*
                AutonomousCarInfo carInfo = autoCarInfoService.get(vin);

                *//**未连接上系统.未连接或者不上传信息*//*
                if(!autoCarInfoService.isConnected(vin) || carInfo == null) {
                    LOG.info("car is not connect: " + car.getVin());
                    map.getCarReqLockMap().put(String.valueOf(vin),"release");
                    map.getCarReqLockMap().remove(vin);
                    continue;
                }
*//*
                if(carInfo.getFromLane() == null && carInfo.getLane() == null){
                    *//**//**尝试去释放一下锁，防止以外情况断连不释放锁*//**//*
                    LOG.warn("car not connect, data cached : " + carInfo.getVin());
                    map.getCarReqLockMap().put(String.valueOf(vin),"release");
                    continue;
                }*//*

                String lane = carInfo.getLane();
                String from_lane = carInfo.getFromLane();

                *//**terminalInfo，from_lane和lane都设置成了-1*//*
                if(lane == null && from_lane == null){
                    LOG.info("car Terminal Info: " + carInfo.getVin() );
                    continue;
                }
                final String[] cur_lane_info = from_lane.split("_");
                final String[] cross_lane_info = lane.split("_");
                *//**全局规划的起点*//*
                if(cur_lane_info[0].equals("cross") && cur_lane_info[1].equals("8888")){
                    LOG.info("car: " + carInfo.getVin() + " req start at: " + carInfo.getLane());
                    map.getCarMap().put(vin,carInfo.getLane());
                }
                *//**全局规划的终点*//*
                else if(cross_lane_info[0].equals("cross") && cross_lane_info[1].equals("9999") ){
                    LOG.info("car: " + carInfo.getVin() + " req end at: " + carInfo.getFromLane());
                    map.getCarMap().put(vin,carInfo.getFromLane());
                }
                else if(cur_lane_info[0].equals("lane") && cross_lane_info[0].equals("cross"))
                {
                    //request Lock
                    LOG.info("car request: " + carInfo.getVin() + " at: " + carInfo.getLane());
                    map.getCarReqLockMap().put(vin,"request");
                    map.getCarMap().put(vin,carInfo.getFromLane());
                }
                else if(cur_lane_info[0].equals("cross") && cross_lane_info[0].equals("lane")){
                    //release lock
                    LOG.info("car release: " + carInfo.getVin() + " at: " + carInfo.getFromLane());
                    map.getCarReqLockMap().put(vin,"release");
                    map.getCarMap().put(vin,carInfo.getFromLane());
                }
            }*/
    }
}
