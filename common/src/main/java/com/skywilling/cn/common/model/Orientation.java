package com.skywilling.cn.common.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class Orientation implements Serializable {

  private static final long serialVersionUID = -3778610732442749919L;
  private double x = 0;
  private double y = 0;
  private double z = 0;
  private double w = 0;
  private int status = -1;
  public Orientation(){}
  public Orientation(double x, double y, double z, double w){
    this.x = x; this.y = y; this.z = z; this.w = w;
  }
}
