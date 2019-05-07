package com.skywilling.cn.web.service.impl;


import com.skywilling.cn.web.model.LockProducer;
import com.skywilling.cn.web.service.NotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotifyServiceKafkaImpl implements NotifyService {

  private static final Logger LOG = LoggerFactory.getLogger(NotifyServiceKafkaImpl.class);
  @Autowired
  private LockProducer lockProducer;

  public void setLockProducer(LockProducer lockProducer) {
    this.lockProducer = lockProducer;
  }

  @Override
  public int returnCar(String vin) {
    lockProducer.writeLockMsg(vin);
    return 0;
  }

  @Override
  public int rentCar(String vin) {
    lockProducer.writeUnlockMsg(vin);
    return 0;
  }
}
