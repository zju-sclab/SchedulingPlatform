package com.skywilling.cn.livemap.service;

import java.util.List;

public interface CarPositionService {

    public List<String> findCarsByLaneName(String laneName, String parkName);

    public String findCarPositionByCarId(String vin, String parkName);
}
