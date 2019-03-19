package com.skywilling.cn.manager.task.service;

import com.skywilling.cn.common.exception.IllegalTaskException;
import com.skywilling.cn.manager.task.model.AutoTask;

import java.util.concurrent.CompletableFuture;

public interface AutoTaskService {

    String submit(AutoTask autoTask) throws IllegalTaskException, IllegalStateException;


    CompletableFuture<Integer> resume(String taskId);

    CompletableFuture<Boolean> stop(String taskId);

    CompletableFuture<Integer> intervening(String taskId);
}
