package com.skywilling.cn.manager.car.model;

import com.skywilling.cn.common.model.AbstractPoint;

public class Instruction extends AbstractPoint {
  private double motor;
  private double servo;
  private int shift;

  public double getMotor() {
    return motor;
  }

  public void setMotor(double motor) {
    this.motor = motor;
  }

  public double getServo() {
    return servo;
  }

  public void setServo(double servo) {
    this.servo = servo;
  }

  public int getShift() {
    return shift;
  }

  public void setShift(int shift) {
    this.shift = shift;
  }
}
