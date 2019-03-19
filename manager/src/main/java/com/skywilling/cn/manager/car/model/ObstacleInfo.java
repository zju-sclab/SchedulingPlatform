package com.skywilling.cn.manager.car.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ObstacleInfo {
    @Id
    private int id;
    private double x;
    private double y;
    private int oriented;
    private double v;
}
