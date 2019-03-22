package com.skywilling.cn.manager.user.model;

import java.io.Serializable;
import java.util.Date;

@Deprecated
public class PwdAuth implements Serializable {

  private static final long serialVersionUID = 7513908928690164641L;
  private Integer uid;
  private String username;
  private String password;
  private String pwdKey;
  private Date gmtModify;
  private Date gmtCreate;

  public Integer getUid() {
    return uid;
  }

  public void setUid(Integer uid) {
    this.uid = uid;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPwdKey() {
    return pwdKey;
  }

  public void setPwdKey(String pwdKey) {
    this.pwdKey = pwdKey;
  }

  public Date getGmtModify() {
    return gmtModify;
  }

  public void setGmtModify(Date gmtModify) {
    this.gmtModify = gmtModify;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }
}
