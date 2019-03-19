package com.skywilling.cn.livemap.service.impl;


import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.livemap.core.StaticMapFactory;
import com.skywilling.cn.livemap.model.*;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
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

    @Override
    public LiveMap getMap(String parkName) {
        if (!maps.containsKey(parkName)) {
            Object read = redisDao.read(PREFIX + parkName);
            if (read != null) {
                maps.putIfAbsent(parkName, (LiveMap) read);
            } else {
                Park park = parkService.queryByName(parkName);
                if (park != null && park.getMapFileUrl() != null) {
                    StaticMapFactory staticMapFactory = new StaticMapFactory();
                    LiveMap liveMap = staticMapFactory.create(park.getMapFileUrl());
                    liveMap.setParkName(parkName);
                    save(liveMap);
                }
            }

        }
        return maps.get(parkName);
    }

    @Override
    public void save(LiveMap map) {
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
