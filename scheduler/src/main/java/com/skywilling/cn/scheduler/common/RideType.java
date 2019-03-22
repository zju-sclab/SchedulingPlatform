package com.skywilling.cn.scheduler.common;

public enum RideType {
  CYCLE(1, "cycle ride"),
  ONE_WAY(0, "one way ride");
  private int code;
  private String desc;

  RideType(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public int getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public static RideType valueOf(int code) {
    for (RideType rideType : RideType.values()) {
      if (code == rideType.getCode()) {
        return rideType;
      }
    }
    return null;
  }
}
