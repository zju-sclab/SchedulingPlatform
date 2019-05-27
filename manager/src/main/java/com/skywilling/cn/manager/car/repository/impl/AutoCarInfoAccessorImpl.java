package com.skywilling.cn.manager.car.repository.impl;


import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.manager.car.model.AutonomousCarInfo;
import com.skywilling.cn.manager.car.repository.AutoCarInfoAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class AutoCarInfoAccessorImpl implements AutoCarInfoAccessor {

  private static final Logger LOG = LoggerFactory.getLogger(AutoCarInfoAccessor.class);
  private static final String PREFIX = "CAR_";

  @Autowired
  private RedisDao redisDao;

  private String generateKey(String vin) {
    return PREFIX + vin;
  }

  @Override
  public void save(AutonomousCarInfo carInfo) {
    String key = generateKey(carInfo.getVin());
    redisDao.save(key, carInfo);
    redisDao.expire(key, 5, TimeUnit.MINUTES);
  }

  @Override
  public AutonomousCarInfo get(String vin) {
    return (AutonomousCarInfo) redisDao.read(generateKey(vin));
  }

  /**
   * todo: this method is not thread-safe, try to fix it in next iteration.
   * */
  @Override
  public AutonomousCarInfo getOrCreate(String vin) {
    AutonomousCarInfo carInfo = get(vin);
    if (carInfo == null) {
      carInfo = new AutonomousCarInfo();
      carInfo.setVin(vin);
      save(carInfo);
    }
    return carInfo;
  }
}