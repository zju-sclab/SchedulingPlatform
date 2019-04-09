package com.skywilling.cn.web.model.view;

import com.skywilling.cn.user.model.UserInfo;

import java.io.Serializable;

public class UserUpdateParam implements Serializable {

  private static final long serialVersionUID = 7366669566334367988L;
  private Integer uid;
  private String username;
  private String realName;
  private String phoneNumber;
  private String email;

  public UserInfo toUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setUid(this.getUid());
    userInfo.setUsername(this.getUsername());
    userInfo.setRealName(this.getRealName());
    userInfo.setPhoneNumber(this.getPhoneNumber());
    userInfo.setEmail(this.getEmail());
    return userInfo;
  }

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
}
