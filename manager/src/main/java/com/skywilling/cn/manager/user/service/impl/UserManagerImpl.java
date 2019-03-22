package com.skywilling.cn.manager.user.service.impl;


import com.skywilling.cn.manager.user.common.VerifyCodeType;
import com.skywilling.cn.manager.user.common.exception.ExpireCodeException;
import com.skywilling.cn.manager.user.common.exception.UnknowAccountException;
import com.skywilling.cn.manager.user.core.PasswordService;
import com.skywilling.cn.manager.user.model.UserInfo;
import com.skywilling.cn.manager.user.service.UserInfoService;
import com.skywilling.cn.manager.user.service.UserManager;
import com.skywilling.cn.manager.user.service.VerifyCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
public class UserManagerImpl implements UserManager {

  @Autowired
  private UserInfoService userInfoService;

  @Autowired
  private VerifyCodeService verifyCodeService;

  @Override
  public UserInfo loginByName(String username, String password) throws UnknowAccountException, AuthenticationException {
    UserInfo userInfo = userInfoService.query(username);
    if (userInfo == null) {
      throw new UnknowAccountException();
    }
    String s = PasswordService.encrypt(password, userInfo.getPwdKey());
    if (!StringUtils.equals(s, userInfo.getPassword())) {
      throw new AuthenticationException();
    }
    return userInfo;
  }

  @Override
  public UserInfo loginByPhone(String phoneNumber, String verifyCode) throws ExpireCodeException, AuthenticationException {
    boolean verify = verifyCodeService.verify(phoneNumber, verifyCode, VerifyCodeType.LOGIN);
    if (!verify) {
      throw new AuthenticationException();
    }
    return userInfoService.queryByPhone(phoneNumber);
  }


  @Override
  public boolean logout(int uid, String token) {
    return false;
  }
}
