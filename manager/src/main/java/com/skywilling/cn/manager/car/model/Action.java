package com.skywilling.cn.manager.car.model;

import com.skywilling.cn.common.model.Node;
import com.skywilling.cn.common.model.RoutePoint;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Action implements Serializable {
    private static final long serialVersionUID = 893452345L;


    private Node outset;
    private Node goal;
    private double v;
    private String type;
    private String laneName;
    private List<RoutePoint> points;
    //private DriveMethod type;
    //private List<Instruction> instructions;

}
