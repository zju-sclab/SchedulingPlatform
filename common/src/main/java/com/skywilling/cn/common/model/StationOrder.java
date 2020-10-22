package com.skywilling.cn.common.model;

import lombok.Data;

@Data
public class StationOrder {
    private String vin;
    private String desName;
    private String time;
    //站点的信息
    private double x, y, z, pitch, roll, yaw;
    //订单当前的状态 0表示创建 1表示正在进行 2表示订单结束
    private int status;
    public StationOrder(String vin,String desName,String time, double x, double y, double z,
                 double pitch, double roll, double yaw){
        this.vin = vin;
        this.desName = desName;
        this.time = time;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
    }
}
