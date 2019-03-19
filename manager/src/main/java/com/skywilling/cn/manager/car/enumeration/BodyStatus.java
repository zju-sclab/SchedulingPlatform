package com.skywilling.cn.manager.car.enumeration;

public enum  BodyStatus {
  FINE(0, "fine"),
  ERROR(1, "have some error"),
  ;

  private int code;
  private String desc;

  BodyStatus(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public int getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }
}
