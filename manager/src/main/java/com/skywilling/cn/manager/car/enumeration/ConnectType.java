package com.skywilling.cn.manager.car.enumeration;

public enum  ConnectType {
  DISCONNECT(0, "disconnect"),
  CONNECT(1, "connect"),
  ;

  private int code;
  private String desc;

  ConnectType(int code, String desc) {
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
