package com.skywilling.cn.common.exception;

public class CarNotExistsException extends Exception {
  private static final long serialVersionUID = -8781132341172026205L;

  public CarNotExistsException(String vin) {
    super(String.format("car %s not exists", vin));
  }
}
