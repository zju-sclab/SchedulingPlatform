package com.skywilling.cn.monitor.model.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName ReleaseLockInfo
 * Author  Lin
 * Date 2019/5/19 16:32
 **/
@Data
public class ReleaseLockInfo implements Serializable {
    private int current_lane_id;
}