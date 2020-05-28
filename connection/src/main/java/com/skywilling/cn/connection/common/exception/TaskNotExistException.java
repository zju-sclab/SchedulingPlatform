package com.skywilling.cn.connection.common.exception;

public class TaskNotExistException extends RuntimeException {
  public TaskNotExistException(String vin) {
    super(String.format("the task of car %s not exist", vin));
  }
}
