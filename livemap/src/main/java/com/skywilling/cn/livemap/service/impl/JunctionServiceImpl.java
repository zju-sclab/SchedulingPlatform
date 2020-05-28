package com.skywilling.cn.livemap.service.impl;


import com.skywilling.cn.livemap.model.LiveJunction;
import com.skywilling.cn.livemap.model.LiveMap;
import com.skywilling.cn.livemap.service.JunctionService;
import com.skywilling.cn.livemap.service.LaneService;
import com.skywilling.cn.livemap.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class JunctionServiceImpl implements JunctionService {
    @Autowired
    MapService mapService;
    @Autowired
    LaneService laneService;

    @Override
    public LiveJunction get(String parkName, String junctionName) {
        LiveMap map = mapService.getMap(parkName);
        if (map != null) {
            return map.getJunctionMap().get(junctionName);
        }
        return null;
    }

    @Override
    public void addJunction(String parkName, LiveJunction junction) {
        LiveMap map = mapService.getMap(parkName);
        if (map != null) {
            map.getJunctionMap().putIfAbsent(junction.getName(), junction);
        }
    }


}
