package com.skywilling.cn.manager.car.enumeration;

public enum TaskState {
  INITIAL(0, "initial"),
  PREPARING(1, "preparing"),
  SUBMITTING(2, "submitting"),
  RUNNING(3, "running"),
  FINISHED(8, "finished"),
  INTERVENTION(6, "intervention"),
  OBSTACLE_INTERRUPTED(0x10, "interrupted"),
  DEVICE_INTERRUPTED(0x18, "device interrupted"),
  RESUMING(0x28, "resuming"),
  KILLING(0x30, "killing"),
  KILLED(0x31, "killed"),
  REJECTED(0x40, "rejected"),
  FINAL_SAVING(0x50, "final saving");
  private int code;
  private String msg;

  TaskState(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  public static TaskState valueOf(int code) {
    for (TaskState state : TaskState.values()) {
      if (code == state.code) {
        return state;
      }
    }
    return null;
  }
}
