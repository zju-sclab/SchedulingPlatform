package com.skywilling.cn.manager.car.enumeration;

public enum DriveType {

  AUTONOMOUS(1, "task"),
  MANUAL(2, "manual"),
  ALL(3, "autonoumou and manual");

  private int code;
  private String desc;

  DriveType(int code, String desc) {
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
