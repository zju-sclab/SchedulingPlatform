package com.skywilling.cn.livemap.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 多车道组成一个edge，路段
 */
public class LiveEdge implements Serializable {
    private String name;
    private Integer id;
    private HashMap<Integer,String> lanesMap;
    private LiveJunction from;
    private LiveJunction to;

}
