package com.skywilling.cn.livemap.model;

import com.skywilling.cn.common.model.RoutePoint;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class LaneShape implements Serializable {

    private static final long serialVersionUID = -4066975799803762308L;
    private int               id;
    private String            name;
    private String            parkName;
    private Double            length;
    private String            type;
    private double            priority;
    private List<RoutePoint>  path;
    private String            fromId;
    private String            toId;
    private double             v;

}
