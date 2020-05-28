package com.skywilling.cn.manager.car.enumeration;

public enum UseStatus {

  FREE(0, "free"),
  RESERVE(1, "reserve"),
  RENT(2, "rent"),
  ;

  private int code;
  private String desc;

  UseStatus(int code, String desc) {
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
