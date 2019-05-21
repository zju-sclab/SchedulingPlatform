package com.skywilling.cn.scheduler.model;

import com.skywilling.cn.common.model.Coordinate;
import lombok.Data;

import java.io.Serializable;

@Data
public class Pose implements Serializable,Cloneable {

  private Integer id;
  private Position position;
  private Orientation orientation;

  public void setPosition(Double x, Double y, Double z){
    this.position = new Position(x,y,z);
  }

  public void setPosition(String x, String y, String z){
    this.position = new Position(x,y,z);
  }

  public void setOrientation(Double x, Double y, Double z, Double w){
    this.orientation = new Orientation(x,y,z,w);
  }

  public void setOrientation(String x, String y, String z, String w){
    this.orientation = new Orientation(x,y,z,w);
  }

  @Override
  public Object clone(){
    Pose pc = null;
    try{
      pc = (Pose)super.clone();
      pc.position = (Position)position.clone();
      pc.orientation = (Orientation)orientation.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return pc;
  }

}
