package com.skywilling.cn.livemap.service;



import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LaneService {
    LiveLane get(String parkName, String laneName);
    void addLane(String parkName, LiveLane liveLane);
    void addVehicles(AutonomousCarInfo car);
    void removeVehicles(AutonomousCarInfo car);
    List<String> getVehicles(String parkName, String laneName);
    void setWeight(String parkName, String laneName, double weight);

}
