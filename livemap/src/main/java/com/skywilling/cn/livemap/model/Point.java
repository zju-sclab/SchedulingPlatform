package com.skywilling.cn.livemap.model;

import com.skywilling.cn.common.model.Coordinate;
import lombok.Data;

import java.io.Serializable;

@Data
public class Point implements Serializable {

  private static final long serialVersionUID = -6776766621769031316L;
  private double x;
  private double y;
  private double z;
  private int status = -1;

  public Coordinate toCoordinate() {
    return new Coordinate(x, y);
  }
}
