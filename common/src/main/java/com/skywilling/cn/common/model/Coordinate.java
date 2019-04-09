package com.skywilling.cn.common.model;


import lombok.Data;

@Data
public class Coordinate extends AbstractPoint {
  private double x;
  private double y;

  public Coordinate() {
  }

  public Coordinate(double x, double y) {
    this.x = x;
    this.y = y;
  }

}
