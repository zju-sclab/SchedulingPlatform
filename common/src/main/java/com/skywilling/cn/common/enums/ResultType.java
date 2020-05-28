package com.skywilling.cn.common.enums;

public enum ResultType {
  SUCCESS(0, "success"),
  FAILED(1, "failed"),
  CAR_NOT_CONNECTED(2, "car not connected"),
  CAR_NOT_EXISTS(3, "car not exists"),
  LOCKED(1000, "locked"),
  UNLOCKED(1001, "unlocked"),
  INTERNAL_ERROR(5, "internal error"),
  UNKNOWN_TYPE(6, "unknown type"),
  ILLEGAL_PARAMETERS(7, "illegal parameters");
  private int code;
  private String msg;

  ResultType(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static ResultType valueOf(int code) {
    for (ResultType resultType : ResultType.values()) {
      if (code == resultType.code) {
        return resultType;
      }
    }
    return null;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  @Override
  public String toString() {
    return "ResultType{" +
        "code=" + code +
        ", msg='" + msg + '\'' +
        '}';
  }
}
