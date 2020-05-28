package com.skywilling.cn.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName Node_Json
 * Author  Lin
 * Date 2019/5/29 20:37
 **/
@Data
public class Node_Json implements Serializable {
    String name;
    double x;
    double y;
    public Node_Json(){}
    public Node_Json(Node node){
        name = node.getName();
        x = node.getX();
        y = node.getY();
    }
}
