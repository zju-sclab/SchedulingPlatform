package com.skywilling.cn.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceImpl implements ExecutorService {

  private static final Logger LOG = LoggerFactory.getLogger(ExecutorServiceImpl.class);

  public ExecutorServiceImpl() {
    workerQueue = new LinkedBlockingQueue<>();
    drivingExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2, 10, TimeUnit.SECONDS, workerQueue,
        new ThreadPoolExecutor.AbortPolicy());
  }

  private ThreadPoolExecutor drivingExecutor;
  private BlockingQueue<Runnable> workerQueue;

  @Override
  public boolean submit(Runnable runnable) {
    try {
      drivingExecutor.submit(runnable);
      return true;
    } catch (Exception e) {
      LOG.warn("[submit]", "queue is full");
    }
    return false;
  }
}
