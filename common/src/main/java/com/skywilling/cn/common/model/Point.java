package com.skywilling.cn.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Point implements Serializable {

  private static final long serialVersionUID = -6776766621769031316L;
  private double x;
  private double y;
  private double z;
  private int status = -1;
  public Point(){}
  public Point(double x, double y, double z){
    this.x = x;
    this.y = y;
    this.z = z;
  }
  public Coordinate toCoordinate() {
    return new Coordinate(x, y);
  }
}
