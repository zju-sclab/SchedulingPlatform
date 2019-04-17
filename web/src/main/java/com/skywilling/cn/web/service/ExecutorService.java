package com.skywilling.cn.web.service;

public interface ExecutorService {

  /**
   * submit runnable
   */
  boolean submit(Runnable runnable);
}
