package com.skywilling.cn.scheduler.model;

import com.skywilling.cn.livemap.model.LiveLane;
import com.skywilling.cn.common.model.Node;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 路径规划结果
 */
@Data
public class Route implements Serializable {

    private int id;
    private String vin;
    private String parkName;
    private List<LiveLane> liveLanes;
    private List<String> lanes;
    private List<Double> times;
    private Node from;
    private Node to;

}
