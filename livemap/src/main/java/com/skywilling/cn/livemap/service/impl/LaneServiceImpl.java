package com.skywilling.cn.livemap.service.impl;

import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.service.LaneService;
import com.skywilling.cn.livemap.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LaneServiceImpl implements LaneService {
    @Autowired
    MapService mapService;

    @Override
    public LiveLane getLane(String parkName, String laneId) {
        return mapService.getMap(parkName).getLaneMap().get(laneId);
    }

    @Override
    public void addLane(String parkName, LiveLane liveLane) {
        if (mapService.getMap(parkName) != null) {
            mapService.getMap(parkName).getLaneMap().put(String.valueOf(liveLane.getId()), liveLane);
        }
    }

    @Override
    public void addVehicles(LiveLane liveLane,String vin) {

    }

    @Override
    public void removeVehicles(LiveLane liveLane,String vin) {


    }

    @Override
    public List<String> getVehicles(String parkName, String laneId) {
        LiveLane liveLane = getLane(parkName,laneId);
        //Set<String> res = liveLane.getVehicles_time_table().keySet();
        List<String> list = new ArrayList<>();
        //list.addAll(res);
       return list;
    }

    @Override
    public void setWeight(String parkName, String laneName, double weight) {
        LiveLane liveLane = this.getLane(parkName, parkName);
        liveLane.setPriority(weight);
    }


}
