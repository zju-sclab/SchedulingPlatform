package com.skywilling.cn.manager.user.common.exception;

public class AuthorizationException extends Exception {
  private static final long serialVersionUID = -3428841767461249718L;

  public AuthorizationException() {
    super();
  }

  public AuthorizationException(String message) {
    super(message);
  }
}
