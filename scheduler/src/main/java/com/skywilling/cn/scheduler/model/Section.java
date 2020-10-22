package com.skywilling.cn.scheduler.model;

import com.skywilling.cn.common.model.RoutePoint;

import java.io.Serializable;
import java.util.List;

public class Section implements Serializable {
    private List<RoutePoint> sectionPoint;
    private boolean is_Lane;
    private int lane_id;
    private int cross_id;
}
