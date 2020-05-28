package com.skywilling.cn.connection.model;

public enum ProtocolField {
  HAS_NEXT("hasNext"),
  TASK_LIST("taskCmd_list"),
  DATA("data"),
  VERSION("version"),
  TYPEINFO("type"),
  VIN("vin"),
  ACK("ack"),
  GEARS("gears"),
  DRIVEMODE("mode"),
  SPEED("speed"),
  STEER("steer"),
  OFFSET("offset"),
  ATTACH("data"),
  TYPE("type"),
  VALUE("value"),
  TIMESTAMP("timestamp"),
  LATITUDE("latitude"),
  LONGITUDE("longitude"),
  ALTITUDE("altitude"),
  CAN("can"),
  GPS("gps"),
  CAMERA("camera");

  private String field;

  ProtocolField(String field) {
    this.field = field;
  }

  public String getField() {
    return field;
  }
}
