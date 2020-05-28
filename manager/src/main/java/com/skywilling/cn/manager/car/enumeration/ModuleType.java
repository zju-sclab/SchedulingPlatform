package com.skywilling.cn.manager.car.enumeration;

public enum ModuleType {

  HARDWARE(1, "hardware"),
  SOFTWARE(0, "software"),
  ;

  private int code;
  private String desc;

  ModuleType(int code, String desc) {
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
