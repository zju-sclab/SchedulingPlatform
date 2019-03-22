package com.skywilling.cn.manager.user.common.exception;

public class ExpireCodeException extends Exception {

  private static final long serialVersionUID = 9211675477380071673L;

  public ExpireCodeException() {
    super("the verify code is expired");
  }

  public ExpireCodeException(String message) {
    super(message);
  }
}
