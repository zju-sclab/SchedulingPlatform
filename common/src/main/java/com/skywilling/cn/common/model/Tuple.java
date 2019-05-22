package com.skywilling.cn.common.model;

public class Tuple<A, B> {
    public final A first;
    public final B second;

    public Tuple(A a, B b){
        first = a;
        second = b;
    }
}
