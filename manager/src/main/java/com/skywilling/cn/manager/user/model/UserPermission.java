package com.skywilling.cn.manager.user.model;

import java.io.Serializable;
import java.util.Date;

public class UserPermission implements Serializable {
  private static final long serialVersionUID = -6677484946576838475L;
  private Integer id;
  private String permission;
  private String desc;
  private Boolean available;
  private Date gmtModify;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public Boolean getAvailable() {
    return available;
  }

  public void setAvailable(Boolean available) {
    this.available = available;
  }

  public Date getGmtModify() {
    return gmtModify;
  }

  public void setGmtModify(Date gmtModify) {
    this.gmtModify = gmtModify;
  }
}
