package com.skywilling.cn.manager.car.model;

import com.skywilling.cn.common.model.LidarPoint;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Action implements Serializable {
    private static final long serialVersionUID = 893452345L;


    private String from;
    private String to;
    private String laneName;
    private double v;
    private List<LidarPoint> points;
    // private DriveMethod type;
   // private List<Instruction> instructions;

}
