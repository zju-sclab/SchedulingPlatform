package com.skywilling.cn.manager.user.common.exception;

public class UnknowAccountException extends Exception {
  private static final long serialVersionUID = -6289735113930660035L;

  public UnknowAccountException() {
    super();
  }

  public UnknowAccountException(String message) {
    super(message);
  }
}
