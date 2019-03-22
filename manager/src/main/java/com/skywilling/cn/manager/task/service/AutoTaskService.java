package com.skywilling.cn.manager.task.service;

import com.skywilling.cn.common.exception.IllegalTaskException;
import com.skywilling.cn.manager.task.model.AutoTask;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.concurrent.CompletableFuture;

public interface AutoTaskService {

    CompletableFuture<Boolean> submit(AutoTask autoTask) ;

    CompletableFuture<Boolean> start(AutoTask autoTask);

    CompletableFuture<Boolean> resume(String taskId);

    CompletableFuture<Boolean> stop(String taskId);

    CompletableFuture<Boolean> intervening(String taskId);
}
