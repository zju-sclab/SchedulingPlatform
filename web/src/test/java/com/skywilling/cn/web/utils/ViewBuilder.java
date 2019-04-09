package com.skywilling.cn.web.utils;

import com.github.pagehelper.PageInfo;

import com.skywilling.cn.livemap.model.Park;
import com.skywilling.cn.manager.user.model.UserInfo;
import com.skywilling.cn.web.model.view.GPSPoint;
import com.skywilling.cn.web.model.view.PageView;
import com.skywilling.cn.web.model.view.ParkView;
import com.skywilling.cn.web.model.view.UserView;
import org.springframework.data.domain.PageImpl;

public class ViewBuilder {

  public static ParkView build(Park park) {
    ParkView parkView = new ParkView();
    parkView.setId(park.getId());
    parkView.setName(park.getName());
    parkView.setZh(park.getZh());
    parkView.setPicture(park.getImgUrl());
    parkView.setUpLeft(   new GPSPoint(park.getMaxy(), park.getMinx()));
    parkView.setUpRight(  new GPSPoint(park.getMaxy(), park.getMaxx()));
    parkView.setDownLeft( new GPSPoint(park.getMiny(), park.getMinx()));
    parkView.setDownRight(new GPSPoint(park.getMiny(), park.getMaxx()));
    return parkView;
  }

  public static UserView build(UserInfo userInfo) {
    UserView userView = new UserView();
    userView.setUid(userInfo.getUid());
    userView.setUsername(userInfo.getUsername());
    userView.setPhoneNumber(userInfo.getPhoneNumber());
    if (userInfo.getRole() != null) {
      userView.setRole(userInfo.getRole().getRole());
    }
    userView.setEmail(userInfo.getEmail());
    return userView;
  }

  public static PageView build(PageInfo pageInfo) {
    PageView pageView = new PageView();
    pageView.setList(pageInfo.getList());
    pageView.setPageNum(pageInfo.getPageNum());
    pageView.setPageSize(pageInfo.getPageSize());
    pageView.setSize(pageInfo.getSize());
    pageView.setTotal(pageInfo.getTotal());
    pageView.setPages(pageInfo.getPages());
    return pageView;
  }

  public static PageView build(PageImpl page) {
    PageView pageView = new PageView();
    pageView.setList(page.getContent());
    pageView.setPageNum(page.getNumber());
    pageView.setPageSize(page.getSize());
    pageView.setSize(page.getNumberOfElements());
    pageView.setTotal(page.getTotalElements());
    pageView.setPages(page.getTotalPages());
    return pageView;
  }


//  public static CarDynamicView build(CarDynamic carDynamic) {
//    CarDynamicView dynamicView = new CarDynamicView();
//    dynamicView.setVin(carDynamic.getVin());
//    dynamicView.setPlate(carDynamic.getCarPlate());
//    dynamicView.setHealth(carDynamic.getHealthState());
//    dynamicView.setEnergy(carDynamic.getEnergy() / 100.0);
//    dynamicView.setEndurance(carDynamic.getEndurance());
//    dynamicView.setType(carDynamic.getDriveMode());
//
//    Station station = new Station();
//    station.setStationId(carDynamic.getStation());
//    station.setName(carDynamic.getStation());
//    station.setLongitude(carDynamic.getLongitude());
//    station.setLatitude(carDynamic.getLatitude());
//
//    dynamicView.setPosition(station);
//    return dynamicView;
//  }
}
