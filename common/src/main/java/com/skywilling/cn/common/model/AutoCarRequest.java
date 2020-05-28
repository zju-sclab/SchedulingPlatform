package com.skywilling.cn.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName AutoCarRequest
 * Author  Lin
 * Date 2019/5/30 22:06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoCarRequest implements Serializable {
    private String vin;
    private boolean lock;
    private String lane_id;
    private String cross_id;

}
