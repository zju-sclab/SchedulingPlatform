package com.skywilling.cn.monitor.model.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName RequestLockInfo
 * Author  Lin
 * Date 2019/5/19 16:30
 **/

@Data
public class RequestLockInfo implements Serializable {
    private int current_lane_id;
    private int target_cross_id;
}
