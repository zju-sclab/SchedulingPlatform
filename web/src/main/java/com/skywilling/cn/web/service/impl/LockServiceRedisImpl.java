package com.skywilling.cn.web.service.impl;

import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.common.exception.CarNotExistsException;
import com.skywilling.cn.web.model.CarKey;
import com.skywilling.cn.web.service.ExecutorService;
import com.skywilling.cn.web.service.LockService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Component
public class LockServiceRedisImpl implements LockService {

  private static final Logger LOG = LoggerFactory.getLogger(LockServiceRedisImpl.class);
  private static final String PREFIX = LockServiceRedisImpl.class.getSimpleName();

  @Autowired
  private RedisDao redisDao;

  private String generateKey(String vin) {
    return String.format("%s_%s", PREFIX, vin);
  }

  private CarKey createCarKey(String vin) {
    CarKey carKey = new CarKey();
    carKey.setLocked(true);
    carKey.setVerifyCode(null);
    carKey.setKey(null);
    carKey.setVin(vin);
    return carKey;
  }

  @Override
  public void lock(String vin) {
    CarKey carKey = (CarKey) redisDao.read(generateKey(vin));
    if (carKey == null) {
      carKey = createCarKey(vin);
    }
    carKey.setVerifyCode(null);
    carKey.setLocked(true);
    redisDao.save(generateKey(vin), carKey);
  }

  @Override
  public void updateKey(String vin, String key) {
    CarKey carKey = (CarKey) redisDao.read(generateKey(vin));
    if (carKey == null) {
      carKey = createCarKey(vin);
      carKey.setKey(key);
    }
    carKey.setKey(key);
    redisDao.save(generateKey(vin), carKey);
  }

  @Override
  public void updateVerifyCode(String vin, String verifyCode) {
    CarKey carKey = (CarKey) redisDao.read(generateKey(vin));
    if (carKey == null) {
      carKey = createCarKey(vin);
      carKey.setVerifyCode(verifyCode);
    }
    carKey.setVerifyCode(verifyCode);
    redisDao.save(generateKey(vin), carKey);
  }

  @Override
  public boolean unlockByKey(String vin, String key) {
    CarKey carKey = (CarKey) redisDao.read(generateKey(vin));
    if (carKey == null) {
      try {
        throw new CarNotExistsException(String.format("%s not exists", vin));
      } catch (CarNotExistsException e) {
        e.printStackTrace();
      }
    }
    if (StringUtils.equals(key, carKey.getKey())) {
      carKey.setLocked(false);
//      lockProducer.writeUnlockMsg(vin);
      redisDao.save(generateKey(vin), carKey);
      return true;
    }
    return false;
  }

  @Override
  public boolean unlockByVerifyCode(String vin, String verificationCode) {
    CarKey carKey = (CarKey) redisDao.read(generateKey(vin));
    if (carKey == null) {
      try {
        throw new CarNotExistsException(String.format("%s not exists", vin));
      } catch (CarNotExistsException e) {
        e.printStackTrace();
      }
    }
    if (StringUtils.equals(verificationCode, carKey.getVerifyCode())) {
      carKey.setLocked(false);
      redisDao.save(generateKey(vin), carKey);
      return true;
    }
    return false;
  }

  @Override
  public boolean isLocked(String vin) {
    CarKey carKey = (CarKey) redisDao.read(generateKey(vin));
    if (carKey == null) {
      try {
        throw new CarNotExistsException(String.format("%s not exists", vin));
      } catch (CarNotExistsException e) {
        e.printStackTrace();
      }
    }
    if (carKey.isLocked()) {
      return true;
    }
    return false;
  }

  public static class ExecutorServiceImpl implements ExecutorService {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutorServiceImpl.class);

    public ExecutorServiceImpl() {
      workerQueue = new LinkedBlockingQueue<>();
      drivingExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
          Runtime.getRuntime().availableProcessors() * 2, 10, TimeUnit.SECONDS, workerQueue,
          new ThreadPoolExecutor.AbortPolicy());
    }

    private ThreadPoolExecutor drivingExecutor;
    private BlockingQueue<Runnable> workerQueue;

    @Override
    public boolean submit(Runnable runnable) {
      try {
        drivingExecutor.submit(runnable);
        return true;
      } catch (Exception e) {
        LOG.warn("[submit]", "queue is full");
      }
      return false;
    }
  }
}
