package com.skywilling.cn.livemap.service.impl;



import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.livemap.service.LaneService;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaneServiceImpl implements LaneService {
    @Autowired
    MapService mapService;
    @Autowired
    CarDynamicService carDynamicService;

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
    public void addVehicles(AutonomousCarInfo car) {
        LiveLane liveLane = this.get(carDynamicService.query(car.getVin()).getParkName(), car.getLane());
        if (!liveLane.getVehicles().contains(car.getVin())) {
            try {
                liveLane.getVehicles().put(car.getVin());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeVehicles(AutonomousCarInfo car) {
        LiveLane liveLane = this.get(carDynamicService.query(car.getVin()).getParkName(), car.getLane());
        if (liveLane.getVehicles().contains(car.getVin())) {
            liveLane.getVehicles().remove(car.getVin());
        }
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
