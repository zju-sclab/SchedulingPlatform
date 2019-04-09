package com.skywilling.cn.common.model;

public enum ModuleState {
  OK(0, "OK"),
  NOT_STARTED(1, "not started"),
  DEVICE_NOT_FOUND(2, "device not found"),
  DEVICE_ERROR(3, "device error"),
  MODULE_WARNING(0x10, "module warning"),
  MODULE_ERROR(0x11, "module error");

  private int state;
  private String desc;

  ModuleState(int state, String desc) {
    this.state = state;
    this.desc = desc;
  }

  public int getState() {
    return state;
  }

  public String getDesc() {
    return desc;
  }

  public static ModuleState valueOf(int state) {
    for (ModuleState moduleState : ModuleState.values()) {
      if (moduleState.getState() == state) {
        return moduleState;
      }
    }
    return null;
  }
}
