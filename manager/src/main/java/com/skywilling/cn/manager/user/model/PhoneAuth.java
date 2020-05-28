package com.skywilling.cn.manager.user.model;

import java.io.Serializable;
import java.util.Date;

@Deprecated
public class PhoneAuth implements Serializable {

  private static final long serialVersionUID = -4856843917691407944L;
  private Integer uid;
  private String phone;
  private String randomCode;
  private Long expire;
  private Date gmtModify;
  private Date gmtCreate;

  public Integer getUid() {
    return uid;
  }

  public void setUid(Integer uid) {
    this.uid = uid;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getRandomCode() {
    return randomCode;
  }

  public void setRandomCode(String randomCode) {
    this.randomCode = randomCode;
  }

  public Long getExpire() {
    return expire;
  }

  public void setExpire(Long expire) {
    this.expire = expire;
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
