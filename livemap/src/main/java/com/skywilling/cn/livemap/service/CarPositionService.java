package com.skywilling.cn.livemap.service;

import java.util.List;

public interface CarPositionService {

    List<String> findCarsByLaneName(String laneName, String parkName);

    String findCarPositionByCarId(String vin, String parkName);
}
