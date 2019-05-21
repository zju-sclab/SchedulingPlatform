package com.skywilling.cn.scheduler.model;

import com.skywilling.cn.common.model.Coordinate;
import lombok.Data;

import java.io.Serializable;

@Data
public class Position implements Serializable,Cloneable {

//  private static final long serialVersionUID = -6776766621769031316L;
  private double x;
  private double y;
  private double z;

//  private int status = -1;

  public Position(){}

  public Position(double x, double y, double z){
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Position(String x, String y, String z){
    this.x = Double.parseDouble(x);
    this.y = Double.parseDouble(y);
    this.z = Double.parseDouble(z);
  }

  public Coordinate toCoordinate() {
    return new Coordinate(x, y);
  }

  @Override
  public Object clone(){
    Position pc = null;
    try{
      pc = (Position)super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return pc;
  }
}
