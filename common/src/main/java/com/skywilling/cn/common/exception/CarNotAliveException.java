package com.skywilling.cn.common.exception;

public class CarNotAliveException  extends Exception{
  private static final long serialVersionUID = 4052696734014755773L;

  public CarNotAliveException(String vin) {
    super(String.format("vin %s is not alive", vin));
  }
}
