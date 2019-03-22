package com.skywilling.cn.manager.user.service.impl;

import com.skywilling.cn.common.config.redis.RedisDao;
import com.skywilling.cn.manager.user.common.VerifyCodeType;
import com.skywilling.cn.manager.user.common.exception.ExpireCodeException;
import com.skywilling.cn.manager.user.service.VerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

  @Autowired
  private RedisDao redisDao;

  private static final String REGISTER_CODE = "REGISTER_CODE";

  private static final String LOGIN_CODE = "LOGIN_CODE";

  private static final String FORGET_PWD_CODE = "FORGET_PWD_CODE";

  private static final int EXPIRE_INTERVAL = 60;
  private static final TimeUnit EXPIRE_TIMEUNIT = TimeUnit.MINUTES;

  private String generateKey(String phoneNumber, int type) {
    switch (type) {
      case VerifyCodeType.REGISTER: {
        return String.format("%s_%s", REGISTER_CODE, phoneNumber);
      }
      case VerifyCodeType.LOGIN: {
        return String.format("%s_%s", LOGIN_CODE, phoneNumber);
      }
      case VerifyCodeType.FORGET_PWD: {
        return String.format("%s_%s", FORGET_PWD_CODE, phoneNumber);
      }
    }
    return null;
  }

  @Override
  public void save(String phoneNumber, String code, int type) {
    String key = generateKey(phoneNumber, type);
    redisDao.save(key, code);
    redisDao.expire(key, EXPIRE_INTERVAL, EXPIRE_TIMEUNIT);
  }

  @Override
  public boolean verify(String phoneNumber, String code, int type) throws ExpireCodeException {
    String key = generateKey(phoneNumber, type);
    Long expire = redisDao.getExpire(key, EXPIRE_TIMEUNIT);
    if (expire <= 0) {
      throw new ExpireCodeException();
    }
    Object read = redisDao.read(key);
    if (code.equalsIgnoreCase((String) read)) {
      redisDao.delete(key);
      return true;
    }
    return false;
  }
}
