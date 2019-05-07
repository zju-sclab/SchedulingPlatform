package com.skywilling.cn.web.service;


public interface LockService {

  /**
   * */
  void updateKey(String vin, String key);

  void updateVerifyCode(String vin, String key);

  /**
   * invalid verifyCode
   */
   void lock(String vin);

  /**
   * */
   boolean unlockByKey(String vin, String key);

  /**
   * */
   boolean unlockByVerifyCode(String vin, String verificationCode);

  /**
   *
   * */
   boolean isLocked(String vin);


}
