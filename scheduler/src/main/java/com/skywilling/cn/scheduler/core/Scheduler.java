package com.skywilling.cn.scheduler.core;

import com.alibaba.druid.util.StringUtils;
import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.scheduler.model.Route;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class Scheduler {

  public static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

  /**
   * split the ride into several segment. each segment is a task. link_0->link_1->link_2->link_3->link_4
   * [0, 1), [1, 3), [3, 5) link_0, link_1->link_2, link_3->link_4
   *
   * for active scheduling.
   */
  @Data
  public static class NodeLock {
      private String name;
      private String owner;
      private ReentrantLock lock = new ReentrantLock();
      private long startTime;
      private PriorityBlockingQueue<Element> inComingVehicles = new PriorityBlockingQueue<>();

      public NodeLock(String name) {
          this.name = name;
      }

      static class Element implements Comparable<Element> {
          String vin;
          CompletableFuture<Boolean> result;
          double priority;

          public Element(String vin, CompletableFuture<Boolean> result, double priority) {
              this.vin = vin;
              this.result = result;
              this.priority = priority;
          }

          public String getVin() {
              return vin;
          }

          @Override
          public int compareTo(Element o) {
              if (this.priority > o.priority) return 1;
              if (this.priority < o.priority) return -1;
              return 0;
          }
      }
     /**轮询某辆车是否已在队列*/
      public boolean hasCar(String vin){
          for(Element car : inComingVehicles){
              if(car.getVin().equals(vin)){
                  return true;
              }
          }
          return false;
      }

      public CompletableFuture<Boolean> acquire(String vin, double priority) {
          CompletableFuture<Boolean> result = new CompletableFuture<>();
          try {
              lock.lock();
              if (owner == null || StringUtils.equals(owner, vin)) {
                  LOG.warn("vin: " + vin + " acquire lock and be the owner of " + name);
                  owner = vin;
                  result.complete(true);
              } else {
                  LOG.warn("vin: " + vin + " acquire lock and wait in queue ! ");
                  Element element = new Element(vin, result, priority);
                  inComingVehicles.offer(element);
              }
          } finally {
              lock.unlock();
          }
          return result;
      }

      public String release(String vin) {
          if(vin == null) return null;
          if (!owner.equalsIgnoreCase(vin)) {
              LOG.warn("vin: " + vin + " release lock and invalid ! ");
              return null;
          }
          try {
              lock.lock();
              Element front = inComingVehicles.poll();
              if (front != null) {
                  LOG.warn("vin: " + vin + " release lock and notify vin : " + front.vin);
                  owner = front.vin;
                  front.result.complete(true);
              } else {
                  LOG.warn("vin: " + vin + " release lock and notify nobody! ");
                  owner = null;
              }
          } finally {
              lock.unlock();
          }
          return owner;
      }

  }
}
