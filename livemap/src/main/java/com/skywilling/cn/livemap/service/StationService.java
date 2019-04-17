package com.skywilling.cn.livemap.service;

import com.skywilling.cn.livemap.model.LiveStation;

import java.util.List;

public interface StationService {

    void addStation(String parkName, LiveStation station);
    LiveStation getStation(String parkName, String stationName);
    void addVehicle(LiveStation station, String vin);
    void removeVehicle(LiveStation station, String vin);
    List<String> getVehicles(String parkName, LiveStation station);

}
