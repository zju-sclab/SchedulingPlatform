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

    @Override
    public LiveMap getMap(String parkName) {
        if (!maps.containsKey(parkName)) {
            Object read = redisDao.read(PREFIX + parkName);
            if (read != null) {
                maps.putIfAbsent(parkName, (LiveMap) read);
            } else {
                Park park = parkService.queryByName(parkName);
                if (park != null && park.getMapFileUrl() != null) {
                    LiveMap liveMap = staticMapFactory.create(park.getMapFileUrl());
                    add(liveMap);
                    shapeMapService.create(park.getShapeFileUrl());
                    maps.putIfAbsent(liveMap.getParkName(),liveMap);
                }
            }

        }
        return maps.get(parkName);
    }

    @Override
    public void add(LiveMap map) {
        maps.putIfAbsent(map.getParkName(), map);
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
