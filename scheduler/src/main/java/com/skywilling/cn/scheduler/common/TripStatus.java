package com.skywilling.cn.scheduler.common;

public enum TripStatus {
  CREATED(0, "ride has been created"),
  SUBMITTED(1, "ride has been submitted"),
  RUNNING(2, "ride is running"),
  FINISHED(8, "ride has FINISHED"),
  KILLING(0x30, "ride is killing"),
  KILLED(0x31, "ride has been killed"),
  REJECTED(0x40, "rejected"),
  FINAL_SAVING(0x50, "final saving");
  private int code;
  private String desc;

  TripStatus(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public static TripStatus valueOf(int code) {
    for (TripStatus r: TripStatus.values()) {
      if (r.code == code) {
        return r;
      }
    }
    return null;
  }
}
