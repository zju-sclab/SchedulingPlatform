package com.skywilling.cn.manager.user.service;


import com.skywilling.cn.manager.user.common.exception.ExpireCodeException;
import com.skywilling.cn.manager.user.common.exception.UnknowAccountException;
import com.skywilling.cn.manager.user.model.UserInfo;

import javax.naming.AuthenticationException;

public interface UserManager {

  UserInfo loginByName(String username, String password) throws UnknowAccountException, AuthenticationException;

  UserInfo loginByPhone(String phoneNumber, String code) throws ExpireCodeException, AuthenticationException;

  boolean logout(int uid, String token);
}
