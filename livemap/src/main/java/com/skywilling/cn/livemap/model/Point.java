package com.skywilling.cn.livemap.model;

import com.skywilling.cn.common.model.Coordinate;

import java.io.Serializable;

public class Point implements Serializable {

  private static final long serialVersionUID = -6776766621769031316L;
  private double x;
  private double y;
  private double z;
  private int status = -1;

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getZ() {
    return z;
  }

  public void setZ(double z) {
    this.z = z;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Coordinate toCoordinate() {
    return new Coordinate(x, y);
  }
}
