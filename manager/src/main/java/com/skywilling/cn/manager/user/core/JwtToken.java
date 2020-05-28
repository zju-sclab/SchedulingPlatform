package com.skywilling.cn.manager.user.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.skywilling.cn.manager.user.common.exception.TokenException;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtToken {

//  token 秘钥
  private static final String SECRET = "yunlecar";
//  过期时间
  private static final int calendarField = Calendar.HOUR;
  private static final int calendarInterval = 2;

  /**
   * 生成jwt token
   * header, payload, signature
   *
   * header:
   *    typ: type 类型，"jwt
   *    alg: algorithm, 加密算法，“HS256”
   *
   *    cnt: content type
   *    kid: key id
   *
   *    private claim
   *
   * payload:
   *    iss: issuer,      jwt签发者
   *    sub: subject,     jwt面向的用户
   *    aud: audience,    jwt接收方
   *    exp: expires at,  过期时间
   *    iat: issue at,    签发时间
   *    jti: jwt id,      jwt唯一身份标识
   *    nbf: not before,  在...时间之前，该jwt不可用
   *
   *    公共声明
   *    私有声明
   *
   *
   * signature:
   *
   * @param uid 用户id
   * @return token
   * @throws UnsupportedEncodingException Algorithm.HMAC256
   */
  public static String createToken(Integer uid) throws UnsupportedEncodingException {
    Date iatDate = new Date();
    //expire time
    Calendar nowTime = Calendar.getInstance();
    nowTime.add(calendarField, calendarInterval);
    Date expTime = nowTime.getTime();

    //header Map
    HashMap<String, Object> headerMap = new HashMap<>();
    headerMap.put("alg", "HS256");
    headerMap.put("typ", "JWT");

    //build token
    return JWT.create().withHeader(headerMap)
        .withClaim("iss", "Service")
        .withClaim("aud", "web")
        .withClaim("uid", null == uid ? null : uid.toString())
        .withIssuedAt(iatDate)
        .withExpiresAt(expTime)
        .sign(Algorithm.HMAC256(SECRET));
  }

  /**
   * 解密token
   * @param token token
   * @return 解密后的token
   */
  public static Map<String, Claim> verifyToken(String token) {

    DecodedJWT jwt = null;
    try {
      JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
      jwt = verifier.verify(token);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    assert jwt != null;
    return jwt.getClaims();
  }

  /**
   * 根据token获取uid
   * @param token token
   * @return uid
   * @throws TokenException error
   */
  public static Integer getUID(String token) throws TokenException {
    Map<String, Claim> claimMap = verifyToken(token);
    Claim uidClaim = claimMap.get("uid");
    if (null == uidClaim || StringUtils.isEmpty(uidClaim.asString())) {
      throw new TokenException("uid is empty");
    }
    return Integer.valueOf(uidClaim.asString());
  }

  /*public static void main(String[] args) throws UnsupportedEncodingException, TokenException {

//    String token = JwtToken.createToken(1);
    String token  = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ3ZWIiLCJ1aWQiOiIxIiwiaXNzIjoiU2VydmljZSIsImV4cCI6MTUzMjY2NzQwOCwiaWF0IjoxNTMyNjYwMjA4fQ.Ns6OxjgeElfGk32CQeF6sgkO3fDqsPVLUMh3LJ-txM4";
    Integer uid = JwtToken.getUID(token);

    System.out.print(uid);
  }*/
}
