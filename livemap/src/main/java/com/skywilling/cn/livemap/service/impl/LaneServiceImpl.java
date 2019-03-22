package com.skywilling.cn.livemap.service.impl;



import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.service.LaneService;
import com.skywilling.cn.livemap.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaneServiceImpl implements LaneService {
    @Autowired
    MapService mapService;


    @Override
    public LiveLane get(String parkName, String laneName) {
        return mapService.getMap(parkName).getLaneMap().get(laneName);
    }

    @Override
    public void addLane(String parkName, LiveLane liveLane) {
        if (mapService.getMap(parkName) != null) {
            mapService.getMap(parkName).getLaneMap().putIfAbsent(liveLane.getName(), liveLane);
        }
    }

    @Override
    public void addVehicles(LiveLane liveLane,String vin) {

    }

    @Override
    public void removeVehicles(LiveLane liveLane,String vin) {
    }

    @Override
    public List<String> getVehicles(String parkName, String laneName) {
        LiveLane liveLane = this.get(parkName, parkName);
        String[] strs = (String[]) liveLane.getVehicles().toArray();
        List<String> collect = Arrays.stream(strs).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void setWeight(String parkName, String laneName, double weight) {
        LiveLane liveLane = this.get(parkName, parkName);
        liveLane.setWeight(weight);
    }


}
