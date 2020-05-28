package com.skywilling.cn.scheduler.common;

import lombok.Data;

@Data
public class EulerAngle {
    private Double roll;
    private Double pitch;
    private Double yaw;

    public EulerAngle(){}

    public EulerAngle(double roll, double pitch, double yaw){
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void setAngle(double roll, double pitch, double yaw){
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }

}
