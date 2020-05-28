package com.skywilling.cn.manager.user.model;

import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;
@Repository
public class UserInfo implements Serializable {

  private static final long serialVersionUID = -5560835541244610765L;
  private Integer uid;
  private UserRole role;
  private String username;
  private String password;
  private String pwdKey;
  private String realName;
  private String phoneNumber;
  private String email;
  private Boolean locked;
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

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
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

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public Boolean getLocked() {
    return locked;
  }

  public void setLocked(Boolean locked) {
    this.locked = locked;
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
