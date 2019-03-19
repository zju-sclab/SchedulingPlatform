package com.skywilling.cn.livemap.exception;

public class ParkNameEmptyException extends Exception {
  private static final long serialVersionUID = -883789469886003507L;

  public ParkNameEmptyException() {
    super();
  }

  public ParkNameEmptyException(String message) {
    super(message);
  }
}
