package com.skywilling.cn.scheduler.model;

import com.alibaba.druid.util.StringUtils;
import lombok.Data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class NodeLock {
    private String name;
    private String owner;
    private ReentrantLock lock = new ReentrantLock();
    private PriorityBlockingQueue<Element> inComingVehicles=new PriorityBlockingQueue<>();

    public NodeLock(String name){
        this.name=name;
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

    public CompletableFuture<Boolean> acquire(String vin,double priority) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        try {
            lock.lock();
            if (owner == null || StringUtils.equals(owner, vin)) {
                owner = vin;
                result.complete(true);
            } else {
                Element element = new Element(vin, result,priority);
                inComingVehicles.put(element);
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    public String release(String vin) {
        if (!owner.equalsIgnoreCase(vin)) {
            return null;
        }
        try {
            lock.lock();
            Element front = inComingVehicles.poll();
            if (front != null) {
                owner = front.vin;
                front.result.complete(true);
            }else {
                owner = null;
            }
        }finally {
            lock.unlock();
        }
        return owner;
    }


}
