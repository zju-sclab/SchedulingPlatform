package com.skywilling.cn.web.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterInfo implements Serializable {

  private static final long serialVersionUID = 3363571691174883976L;
  private String username;
  private String password;
  private String validatePwd;
  private String phoneNumber;
  private String verifyCode;

}
