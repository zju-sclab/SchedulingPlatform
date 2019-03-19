package com.skywilling.cn.manager.car.enumeration;

public enum CarType {
  TAXI(0, "租赁用车"),
  POSTMAN(1, "快递车");

  private int code;
  private String desc;

  CarType(int code, String desc) {
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
