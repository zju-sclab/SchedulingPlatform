package com.skywilling.cn.manager.user.common.enums;

public enum  UserType {
  ADMIN(0, "admin"),
  PARKER(1, "park manager"),
  POSTMAN(2, "postman");

  private int code;
  private String desc;

  UserType(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static UserType valueOf(int code) {
    for (UserType userType : UserType.values()) {
      if (userType.getCode() == code) {
        return userType;
      }
    }
    return null;
  }

  public int getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }
}
