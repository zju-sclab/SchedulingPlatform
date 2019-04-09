package com.skywilling.cn.common.model;

import lombok.Data;

/**
 * ClassName LidarPoint
 * Author  Lin
 * Date 2019/4/8 21:48
 **/
@Data
public class LidarPoint extends AbstractPoint {
    private double px;
    private double py;
    private double pz;
    private double ox;
    private double oy;
    private double oz;
    private double ow;

    public LidarPoint(){ }

    public LidarPoint(String px, String py, String pz, String ox, String oy, String oz, String ow){
        this.px = Double.valueOf(px);
        this.py = Double.valueOf(py);
        this.pz = Double.valueOf(pz);
        this.ox = Double.valueOf(ox);
        this.oy = Double.valueOf(oy);
        this.oz = Double.valueOf(oz);
        this.ow = Double.valueOf(ow);
    }
    private int status = -1;
}
