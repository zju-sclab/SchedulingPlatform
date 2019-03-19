package com.skywilling.cn.monitor.listener;

import org.springframework.stereotype.Component;

@Component
public class BasicListener {

    public void init(){}

    public boolean process(String vin, boolean result,String body){return true;}

    public boolean process(String vin,String body){ return true;}
}
