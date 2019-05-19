package com.skywilling.cn.livemap.service.impl;


import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.livemap.core.StaticMapFactory;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.ShapeMapService;
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
    private ShapeMapService shapeMapService;
    @Autowired
    StaticMapFactory staticMapFactory;

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
                maps.put(parkName, (LiveMap) read);
            }
            else {
                Park park = parkService.queryByName(parkName);
                if (park != null&& park.getMapFileUrl() != null && park.getShapeFileUrl() != null) {
                    //读取本地文件
                    LiveMap liveMap = staticMapFactory.create(parkName, park.getMapFileUrl());
                    addMap(liveMap);
                    //读取本地文件夹
                    shapeMapService.create(parkName);
                    //maps.putIfAbsent(liveMap.getParkName(),liveMap);
                    maps.put(liveMap.getParkName(),liveMap);
                }
            }
        }
        return maps.get(parkName);
    }

    @Override
    public void addMap(LiveMap map) {
        //maps.putIfAbsent(map.getParkName(), map);
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

}
