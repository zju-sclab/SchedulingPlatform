package com.skywilling.cn.manager.car.model;

import java.io.Serializable;

public class Orientation implements Serializable {

  private static final long serialVersionUID = -3778610732442749919L;
  private double roll = 0;
  private double pitch = 0;
  private double yaw = 0;
  private int status = -1;

  public double getRoll() {
    return roll;
  }

  public void setRoll(double roll) {
    this.roll = roll;
  }

  public double getPitch() {
    return pitch;
  }

  public void setPitch(double pitch) {
    this.pitch = pitch;
  }

  public double getYaw() {
    return yaw;
  }

  public void setYaw(double yaw) {
    this.yaw = yaw;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}
