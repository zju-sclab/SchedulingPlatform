package com.skywilling.cn.common.exception;

public class CarNotVinException extends Exception {

  private static final long serialVersionUID = 407553702891659157L;

  public CarNotVinException() {
    super("the vin is empty");
  }
}
