package com.skywilling.cn.livemap.service;

import com.skywilling.cn.livemap.model.LiveStation;

import java.util.List;

public interface StationService {

    void add(String parkName, LiveStation station);
    LiveStation get(String parkName, String stationName);
    List<String> QueryVehicles();
}
