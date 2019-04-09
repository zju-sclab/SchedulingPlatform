package com.skywilling.cn.web.service;


public interface LockService {

  /**
   * */
  public void updateKey(String vin, String key);

  public void updateVerifyCode(String vin, String key);

  /**
   * invalid verifyCode
   */
  public void lock(String vin);

  /**
   * */
  public boolean unlockByKey(String vin, String key);

  /**
   * */
  public boolean unlockByVerifyCode(String vin, String verificationCode);

  /**
   *
   * */
  public boolean isLocked(String vin);


}
