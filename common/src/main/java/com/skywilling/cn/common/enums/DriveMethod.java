package com.skywilling.cn.common.enums;

import org.apache.commons.lang3.StringUtils;

public enum DriveMethod {
  TRJ(1, "trajectory"),
  LANE(2, "lane"),
  REPLAY(3, "replay"),
  SITE(4, "site"),
  LIDAR(5,"lidar")
  ;
  private String type;
  private int code;
  DriveMethod(int code, String type) {
    this.code = code;
    this.type = type;
  }
  public String getType() {
    return type;
  }
  public int getCode() {
    return code;
  }
  public static DriveMethod toDriveMehtod(String value) {
    for (DriveMethod type: DriveMethod.values()) {
      if (StringUtils.equals(value, type.type)) {
        return type;
      }
    }
    return null;
  }
}
