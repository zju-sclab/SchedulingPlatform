package com.skywilling.cn.manager.car.model;

import java.io.Serializable;

public class ModuleInfo implements Serializable {

  private static final long serialVersionUID = 34123412342L;
  private String name;
  private int status;
  private int type;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }


}
