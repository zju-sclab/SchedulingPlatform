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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    @Scheduled(fixedRate = 1000)
    private void syncFile() {
        for (String name : maps.keySet()) {
            redisDao.save(PREFIX + name, maps.get(name));
        }
    }

    /**
     * 每0.1s钟从redis刷新内存数据库的数据到内存的Map
     */
    @Scheduled(fixedRate = 100)
    @Override
    public void upDateReqLockMap(){

            /**从mongoDb拿到所有车俩的实时数据 */
            List<AutonomousCarInfo> autonomousCarInfoList = autoCarInfoGeoAccessor.getAll();
            //System.out.println("map service get all car: " + autonomousCarInfoList.size());
            for(AutonomousCarInfo carInfo : autonomousCarInfoList){
                String vin = carInfo.getVin();
                CarDynamic carDynamic = carDynamicService.query(vin);
                if(carDynamic == null) {
                    //System.out.println("no such car");
                    continue;
                }
                int parkId = carDynamic.getParkId();
                String parkName = parkService.query(parkId).getName();
                LiveMap map = getMap(parkName);
                //System.out.println("map service get car: " + vin);
                /**未连接上系统*/
                if(!autoCarInfoService.isConnected(vin)||carInfo.getFromLane() == null && carInfo.getLane() == null) {
                    /**尝试去释放一下锁*/
                    map.getCarReqLockMap().put(String.valueOf(vin),"release");
                    continue;
                }
                final String[] cur_lane_info = carInfo.getFromLane().split("_");
                final String[] cross_lane_info = carInfo.getLane().split("_");

                if(cur_lane_info[0].equals("cross") && cur_lane_info[1].equals("8888")){
                    map.getCarMap().put(vin,carInfo.getLane());
                }
                else if(cross_lane_info[0].equals("cross") && cross_lane_info[1].equals("9999") ){
                    map.getCarMap().put(vin,carInfo.getFromLane());
                }
                else if(cur_lane_info[0].equals("lane") && cross_lane_info[0].equals("cross"))
                {
                    //request Lock
                    map.getCarReqLockMap().put(vin,"request");
                    map.getCarMap().put(vin,carInfo.getFromLane());
                }
                else if(cur_lane_info[0].equals("cross") && cross_lane_info[0].equals("lane")){
                    //release lock
                    map.getCarReqLockMap().put(vin,"release");
                    map.getCarMap().put(vin,carInfo.getFromLane());
                }
            }
    }
}
