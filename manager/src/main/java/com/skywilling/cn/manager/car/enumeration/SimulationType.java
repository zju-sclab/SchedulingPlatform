package com.skywilling.cn.manager.car.enumeration;

public enum SimulationType {
  NOT(0, "false"),
  IS(1, "true");

  private int code;
  private String desc;

  SimulationType(int code, String desc) {
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
