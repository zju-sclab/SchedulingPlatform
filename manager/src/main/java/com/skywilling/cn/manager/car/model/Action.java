package com.skywilling.cn.manager.car.model;

import com.skywilling.cn.common.enums.DriveMethod;
import com.skywilling.cn.common.model.LidarPoint;
import com.skywilling.cn.livemap.model.Point;
import com.skywilling.cn.manager.car.model.Instruction;
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
    private DriveMethod type;
    private List<LidarPoint> points;

    private List<Instruction> instructions;

}
