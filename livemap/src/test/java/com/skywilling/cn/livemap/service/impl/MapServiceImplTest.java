package com.skywilling.cn.livemap.service.impl;

import com.skywilling.cn.livemap.core.StaticMapFactory;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.ShapeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentHashMap;

public class MapServiceImplTest {
    private static final String PREFIX = "map_";
    private ConcurrentHashMap<String, LiveMap> maps = new ConcurrentHashMap<>();

    @Autowired
    private MapService mapService;
    @Autowired
    StaticMapFactory staticMapFactory;
    @Test
    public void getMap() {
        //LiveMap map = mapService.getMap("yuquanxiaoqu3");
    }

}