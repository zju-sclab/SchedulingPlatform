package com.skywilling.cn.livemap.service.impl;


import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.livemap.core.StaticMapAndShapeFactory;
import com.skywilling.cn.livemap.core.StaticMapFactory;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.ShapeMapService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.AutoCarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MapServiceImpl implements MapService {
    private static final String PREFIX = "map_";
    private ConcurrentHashMap<String, LiveMap> maps = new ConcurrentHashMap<>();
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private ParkService parkService;
    @Autowired
    private ShapeMapService shapeMapService;
    @Autowired
    StaticMapFactory staticMapFactory;
    @Autowired
    StaticMapAndShapeFactory staticMapAndShapeFactory;
    @Autowired
    AutoCarInfoService autoCarInfoService;

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
            Object read = redisDao.read(PREFIX + parkName);
            if (read != null) {
               // maps.putIfAbsent(parkName, (LiveMap) read);
                //map缓存可能更新了
                maps.put(parkName, (LiveMap) read);
            }
            else {
                   LiveMap liveMap = createMapByLidarMap(parkName);
                   maps.put(parkName,liveMap);
            }
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
            shapeMapService.create(parkName);
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
    @Scheduled(fixedRate = 1000)
    private void syncFile() {
        for (String name : maps.keySet()) {
            redisDao.save(PREFIX + name, maps.get(name));
        }
    }

    /**
     * 每0.1s钟从redis刷新数据到内存Map
     */
    @Scheduled(fixedRate = 100)
    private void upDateReqLockMap(){
        for(String mapName: maps.keySet() ){
            LiveMap map = getMap(mapName);
            Set<String> vins = map.getCarsSet();
            for(String vin : vins){
                AutonomousCarInfo carInfo = autoCarInfoService.get(vin);
                if(carInfo.getFromLane() != null && carInfo.getLane() != null
                        &&carInfo.getFromLane().startsWith("lane") && carInfo.getLane().startsWith("curve"))
                {
                    //request Lock
                    map.getCarReqLockMap().put(vin,carInfo.getLane());
                    map.getCarMap().put(vin,carInfo.getFromLane());
                }else if(carInfo.getLane() != null && carInfo.getLane().startsWith("curve")){
                    //release lock
                    map.getCarReqLockMap().put(vin,"");
                    map.getCarMap().put(vin,carInfo.getLane());
                }
            }
        }
    }
}
