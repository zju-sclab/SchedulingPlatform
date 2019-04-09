package com.skywilling.cn.web.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class RideParam implements Serializable {

  private static final double DEFAULT_VELOCITY = 1.5;
  private static final double DEFAULT_ACCELEARTION = 0.5;
  private static final long serialVersionUID = 2498279891299047820L;

  private String vin;
  private String source;
  private String goal;
  private boolean usingMapSpeed = false;
  private double velocity = DEFAULT_VELOCITY;
  private double acceleartion = DEFAULT_ACCELEARTION;

  public RideParam() {
  }

  public RideParam(String vin, String goal, boolean usingMapSpeed, double velocity,
      double acceleartion) {
    this.vin = vin;
    this.goal = goal;
    this.velocity = velocity;
    this.usingMapSpeed = usingMapSpeed;
    this.acceleartion = acceleartion;
  }

  public RideParam(String vin, String goal) {
    this.vin = vin;
    this.goal = goal;
  }

}
