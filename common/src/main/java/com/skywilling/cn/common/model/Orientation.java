package com.skywilling.cn.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Orientation implements Serializable,Cloneable {

//  private static final long serialVersionUID = -6776766621769031316L;
  private double x;
  private double y;
  private double z;
  private double w;
//  private int status = -1;

  public Orientation(){}

  public Orientation(double x, double y, double z, double w){
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public Orientation(String  x, String  y, String  z, String  w){
    this.x = Double.parseDouble(x);
    this.y = Double.parseDouble(y);
    this.z = Double.parseDouble(z);
    this.w = Double.parseDouble(w);
  }
  public Coordinate toCoordinate() {
    return new Coordinate(x, y);
  }

  @Override
  public Object clone(){
    Orientation oc = null;
    try{
      oc = (Orientation)super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return oc;
  }
}
