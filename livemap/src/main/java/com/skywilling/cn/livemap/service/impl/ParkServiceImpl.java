package com.skywilling.cn.livemap.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.skywilling.cn.livemap.db.mapper.ParkMapper;
import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.livemap.service.ParkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParkServiceImpl implements ParkService {

  @Autowired
  private ParkMapper parkMapper;

  @Transactional
  @Override
  public void save(Park park) {
    parkMapper.save(park);
  }

  @Transactional
  @Override
  public void delete(int id) {
    parkMapper.delete(id);
  }

  @Transactional
  @Override
  public void update(Park park) {
    parkMapper.update(park);
  }

  @Override
  public void updateMap(Park parkMap) {
    Park park = new Park();
    park.setId(parkMap.getId());
    park.setName(park.getName());
    park.setImgUrl(parkMap.getImgUrl());
    park.setMinx(parkMap.getMinx());
    park.setMiny(parkMap.getMiny());
    park.setMaxx(parkMap.getMaxx());
    park.setMaxy(parkMap.getMaxy());
    parkMapper.update(park);
  }

  @Override
  public void updateRegion(int parkId, String mapUrl, String shapeUrl) {
    Park park = new Park();
    park.setId(parkId);
    park.setMapFileUrl(mapUrl);
    park.setShapeFileUrl(shapeUrl);
    parkMapper.update(park);
  }

  @Override
  public Park query(int id) {
    return parkMapper.queryById(id);
  }

  @Override
  public Park queryByName(String name) {
    return parkMapper.queryByName(name);
  }

  @Override
  public PageInfo<Park> query(int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> parkMapper.query());
  }
}
