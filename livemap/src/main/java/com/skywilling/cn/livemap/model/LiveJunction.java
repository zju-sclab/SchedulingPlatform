package com.skywilling.cn.livemap.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * crossNode：交叉路口，合流点
 */
@Data
public class LiveJunction extends Node implements Serializable {

    private double priority;
    private double weight;
    private LinkedBlockingQueue<String> inComingVehicles = new LinkedBlockingQueue<>();

    private Set<String> LanesStart;
    private Set<String> LanesEnd;



}
