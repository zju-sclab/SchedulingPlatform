package com.skywilling.cn.livemap.model;


import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *  A link is basically a connection between two Lane
 *  车道间链接
 */
@Data
public class LiveConnection implements Serializable {
    private Integer id;
    private String name;
    private String fromLane;
    private String toLane;

    private LinkedBlockingQueue<String> vehicles;
}
