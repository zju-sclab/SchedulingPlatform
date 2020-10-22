package com.skywilling.cn.livemap.service;

import com.github.pagehelper.PageInfo;
import com.skywilling.cn.livemap.model.Park;

import java.util.List;


public interface ParkService {

  void save(Park park);

  void delete(int id);

  void update(Park park);

  void updateMap(Park parkMap);

  void updateRegion(int parkId, String mapUrl, String shapeUrl);

  Park query(int id);

  Park queryByName(String name);

  List<Park> query();



}
