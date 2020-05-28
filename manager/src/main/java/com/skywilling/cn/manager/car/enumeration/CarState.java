package com.skywilling.cn.manager.car.enumeration;

public enum CarState {
  LOST(-1, "lost"),
  FREE(0, "free"),
  IN_TASK(1, "in task"),
  ;
  private int state;
  private String msg;

  CarState(int state, String msg) {
    this.state = state;
    this.msg = msg;
  }

  public int getState() {
    return state;
  }

  public String getMsg() {
    return msg;
  }

  public static CarState valueOf(int state) {
    for (CarState carState : CarState.values()) {
      if (carState.state == state) {
        return carState;
      }
    }
    return null;
  }
}
