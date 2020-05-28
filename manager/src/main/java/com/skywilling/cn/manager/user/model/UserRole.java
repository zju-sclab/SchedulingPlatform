package com.skywilling.cn.manager.user.model;

import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;
@Repository
public class UserRole implements Serializable {

  private static final long serialVersionUID = -1758449215643524736L;
  private Integer id;
  private String role;
  private String desc;
  private Boolean available;
  private Date gmtModify;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
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
