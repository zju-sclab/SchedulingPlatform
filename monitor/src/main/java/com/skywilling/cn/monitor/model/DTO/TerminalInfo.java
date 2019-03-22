package com.skywilling.cn.monitor.model.DTO;


import com.skywilling.cn.manager.car.model.ModuleInfo;
import com.skywilling.cn.manager.car.model.Pose;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TerminalInfo implements Serializable {

  private static final long serialVersionUID = -6127823095016190773L;

  private double v = 0.0;
  private double wheelAngle = 0.0;
  private double gear = 0;
  private Pose pose = new Pose();
  private List<ModuleInfo> nodes = null;
  private long timestamp;
  private String lane;

  public double getV() {
    return v;
  }

  public void setV(double v) {
    this.v = v;
  }

  public double getWheelAngle() {
    return wheelAngle;
  }

  public void setWheelAngle(double wheelAngle) {
    this.wheelAngle = wheelAngle;
  }

  public double getGear() {
    return gear;
  }

  public void setGear(double gear) {
    this.gear = gear;
  }

  public Pose getPose() {
    return pose;
  }

  public void setPose(Pose pose) {
    this.pose = pose;
  }

  public List<ModuleInfo> getNodes() {
    return nodes;
  }

  public void setNodes(List<ModuleInfo> nodes) {
    this.nodes = nodes;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }


}
