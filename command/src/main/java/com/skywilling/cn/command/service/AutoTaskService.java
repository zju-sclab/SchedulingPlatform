package com.skywilling.cn.command.service;

import com.skywilling.cn.manager.task.model.AutoTask;

import java.util.concurrent.CompletableFuture;

public interface AutoTaskService {

    CompletableFuture<Boolean> submit(AutoTask autoTask) ;

    CompletableFuture<Boolean> start(AutoTask autoTask);

    CompletableFuture<Boolean> resume(String taskId);

    CompletableFuture<Boolean> stopCar(String vin);

    CompletableFuture<Boolean> intervening(String taskId);
}
