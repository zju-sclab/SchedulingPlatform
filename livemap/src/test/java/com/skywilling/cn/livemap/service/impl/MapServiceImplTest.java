package com.skywilling.cn.livemap.service.impl;

import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.livemap.core.StaticMapFactory;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ParkService;
import com.skywilling.cn.livemap.service.ShapeMapService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class MapServiceImplTest {
    private static final String PREFIX = "map_";
    private ConcurrentHashMap<String, LiveMap> maps = new ConcurrentHashMap<>();
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private ParkService parkService;
    @Autowired
    private ShapeMapService shapeMapService;
    @Autowired
    private MapService mapService;
    @Autowired
    StaticMapFactory staticMapFactory;
    @Test
    public void getMap() {
        LiveMap map = mapService.getMap("xuanzhou");
    }

    @Test
    public void addMap() {
        String name = "xuanzhou";
        LiveMap liveMap = new LiveMap();
        liveMap.setParkName("xuanzhou");

    }
}