package com.skywilling.cn.common.enums;

public enum TypeField {
  REGISTRATION(0x00, "registration"),
  LOGIN(0x01, "login"),
  HEARTBEAT(0x02, "heartbeat"),
  LOGOUT(0x03, "logout"),
  MODE_CHANGE(0x04, "mode change"),
  MODULE_CHECK(0x05, "task client check"),
  TASK_REQUEST(0x10, "task request"),
  TASK_REAL_TIME(0x11, "real time task info"),
  GPS_INFO(0x12, "GPS Info"),
  STOP_AUTONOMOUS(0x13, "stop task"),
  OBSTACLE_INFO(0x14, "obstacle info"),
  TERMINAL_INFO(0x15, "terminal info"),
  PREPARE_FIRE(0x20, "prepare fire"),
  FIRE_AUTONOMOUS(0x21, "fire task"),
  FIRE_LANE_AUTONOMOUS(0x22, "fire lane task"),
  TELE_CONTROL(0x23, "command"),
  PAUSE_AUTONOMOUS(0x31,"pause task"),
  CONTINUE_AUTONOMOUS(0x32, "continue task"),
  FIRE_LIDAR_AUTONOMOUS(0x33, "fire lidar task"),
  REQUEST_LOCK(0x41,"request lock"),
  RESPONSE_LOCK(0x42,"respond lock"),
  RELEASE_LOCK(0x43, "release lock"),
  PURSUIT_STATION_POINT(0x50, "pursuit station");

  private final String desc;
  private final int type;

  TypeField(int type, String desc) {
    this.type = type;
    this.desc = desc;
  }

  public static TypeField valueOf(Integer code) {
    for (TypeField type : TypeField.values()) {
      if (type.type == code) {
        return type;
      }
    }
    return null;
  }

  public int getType() {
    return type;
  }

  public String getDesc() {
    return desc;
  }
}
