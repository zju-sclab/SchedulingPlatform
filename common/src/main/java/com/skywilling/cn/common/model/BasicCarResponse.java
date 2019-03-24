package com.skywilling.cn.common.model;


import lombok.Data;

/**
 * tcp,回复车辆
 */
@Data
public class BasicCarResponse {
    private int code;
    private Object attach;

    public static boolean isEmpty(BasicCarResponse response){
        if(response==null){
           return true;
        }
        return false;
    }
}
