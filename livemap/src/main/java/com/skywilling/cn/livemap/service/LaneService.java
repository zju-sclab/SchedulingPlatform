package com.skywilling.cn.livemap.service;



import com.skywilling.cn.livemap.model.LiveLane;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LaneService {
    LiveLane getLane(String parkName, String laneName);
    void addLane(String parkName, LiveLane liveLane);
    void addVehicles(LiveLane liveLane, String vin);
    void removeVehicles(LiveLane liveLane, String vin);
    List<String> getVehicles(String parkName, String laneName);
    void setWeight(String parkName, String laneName, double weight);

}
