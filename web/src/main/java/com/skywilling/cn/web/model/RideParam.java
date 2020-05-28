package com.skywilling.cn.web.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class RideParam implements Serializable {

  private static final double DEFAULT_VELOCITY = 1.5;
  private static final double DEFAULT_ACCELEARTION = 0.5;
  private static final long serialVersionUID = 2498279891299047820L;

  private String vin;
  private String from;
  private String goal;

  private double velocity = DEFAULT_VELOCITY;
  private double acc = DEFAULT_ACCELEARTION;

  public RideParam() {
  }

  public RideParam(String vin, String from, String goal, double velocity, double acc) {
    this.vin = vin;
    this.goal = goal;
    this.from = from;
    this.velocity = velocity;
    this.acc = acc;
  }

  public RideParam(String vin, String from, String goal) {
    this.vin = vin;
    this.from = from;
    this.goal = goal;
  }
  public RideParam(String vin,String goal){
    this.vin = vin;
    this.goal = goal;
  }

}
