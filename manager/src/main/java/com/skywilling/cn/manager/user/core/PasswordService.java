package com.skywilling.cn.manager.user.core;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordService {

  public static String encrypt(String password, String salt) {
    return DigestUtils.md5Hex(password + salt);
  }
}
