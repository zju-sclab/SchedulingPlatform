package com.skywilling.cn.scheduler.service;

import com.skywilling.cn.scheduler.common.TripStatus;
import com.skywilling.cn.scheduler.model.Trip;

public interface TripCallBack {

  void onRideFinished(Trip trip);

  void onRideException(Trip trip, TripStatus tripStatus);
}
