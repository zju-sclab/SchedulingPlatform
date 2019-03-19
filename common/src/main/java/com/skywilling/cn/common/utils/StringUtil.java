package com.skywilling.cn.common.utils;

import java.util.Random;
import java.util.UUID;

public class StringUtil {

  /**
   * 返回UUID字符串
   * @return
   */
  public static String getUUID() {
    return UUID.randomUUID().toString().toUpperCase().replace("-", "");
  }

  /**
   * 返回定长字符串
   * @param len 长度
   */
  public static String getRandomString(int len) {
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < len; ++i) {
      int i1 = random.nextInt(2) % 2 == 0 ? 65 : 97;
      char c = (char) (random.nextInt(26) + i1);
      sb.append(c);
    }
    return sb.toString();
  }

  /**
   * 生成短信验证码
   */
  public static String getVerificationCode() {
    int i = (int) ((Math.random() * 9 + 1) * 100000);
    return Integer.toString(i);
  }

}
