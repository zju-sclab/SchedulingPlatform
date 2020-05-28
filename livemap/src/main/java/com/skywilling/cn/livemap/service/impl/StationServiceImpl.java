package com.skywilling.cn.livemap.service.impl;

import com.skywilling.cn.livemap.model.LiveStation;
import com.skywilling.cn.livemap.service.MapService;
import com.skywilling.cn.livemap.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationServiceImpl implements StationService {
    @Autowired
    MapService mapService;


    @Override
    public void addStation(String parkName, LiveStation station) {
       if(mapService.getMap(parkName) != null){
           mapService.getMap(parkName).getStationMap().putIfAbsent(station.getName(),station);
       }
    }

    @Override
    public LiveStation getStation(String parkName, String stationName) {
        if(mapService.getMap(parkName) != null){
            return  mapService.getMap(parkName).getStationMap().get(stationName);
        }
        return null;
    }



    @Override
    public List<String> getVehicles(String parkName, LiveStation station) {
        List<String> vins = station.getVehicles();
        return vins;
    }

    @Override
    public void addVehicle(LiveStation liveStation, String vin) {
        List<String> vins = liveStation.getVehicles();
        vins.add(vin);

    }

    @Override
    public void removeVehicle(LiveStation station, String vin) {
        List<String> vins = station.getVehicles();
        vins.remove(vin);
    }

}
