package com.skywilling.cn.livemap.service.impl;

import com.skywilling.cn.livemap.model.LiveStation;
import com.skywilling.cn.livemap.service.StationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationServiceImpl implements StationService {
    @Override
    public void add(String parkName, LiveStation station) {

    }

    @Override
    public LiveStation get(String parkName, String stationName) {
        return null;
    }

    @Override
    public List<String> QueryVehicles() {
        return null;
    }
}
