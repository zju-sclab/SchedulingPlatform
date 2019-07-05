package com.skywilling.cn.livemap.model;

import com.skywilling.cn.common.model.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * crossNode：交叉路口，合流点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveJunction extends Node implements Serializable {

    private Set<String> curves = Collections.newSetFromMap(new ConcurrentHashMap<>());
    //排队通过路口的阻塞队列
    private LinkedBlockingQueue<String> inComingVehicles = new LinkedBlockingQueue<>();

}
