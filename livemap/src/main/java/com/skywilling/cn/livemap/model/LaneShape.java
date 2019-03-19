package com.skywilling.cn.livemap.model;

import com.skywilling.cn.common.enums.DriveMethod;
import com.skywilling.cn.common.model.Coordinate;
import com.skywilling.cn.manager.car.model.Instruction;
import lombok.Data;

import java.util.List;

@Data
public class LaneShape {
    private static final long serialVersionUID = -4066975799803762308L;
    private String LaneName;
    private String from;
    private String to;
    private String id;
    private List<Coordinate> path;
    private int supportedMethods = 0;
    private List<Instruction> instructions;
    private SiteDetail siteDetail;
    private int priority;

    public void addSupport(DriveMethod type) {
        supportedMethods |= (1 << type.getCode());
    }

    public boolean isSupport(DriveMethod type) {
        return (supportedMethods & (1 << type.getCode())) != 0;
    }

    public void removeSupport(DriveMethod type) {
        if (isSupport(type))
            supportedMethods ^= (1<<type.getCode());
    }

}
