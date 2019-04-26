package com.skywilling.cn.livemap.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
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

    /**
     * 对应XML文件里的id
     */
    private String name;

    private int id;

    private LinkedBlockingQueue<String> inComingVehicles = new LinkedBlockingQueue<>();

    private Set<String> LanesStart = new HashSet<>();

    private Set<String> LanesEnd = new HashSet<>();

}
