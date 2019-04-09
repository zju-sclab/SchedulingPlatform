package com.skywilling.cn.web.model.view;



import com.skywilling.cn.manager.user.model.UserInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddParam implements Serializable {

  private static final long serialVersionUID = -4607663117945310693L;
  private String username;
  private String realName;
  private Integer roleId;
  private String phoneNumber;
  private String email;
  private Boolean locked;

  public UserInfo toUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setUsername(this.getUsername());
    userInfo.setRealName(this.getRealName());
    userInfo.setPhoneNumber(this.getPhoneNumber());
    userInfo.setEmail(this.getPhoneNumber());
    userInfo.setLocked(this.locked);
    userInfo.setPassword("abc123");
    return userInfo;
  }

}
