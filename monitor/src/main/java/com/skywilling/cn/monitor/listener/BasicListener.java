package com.skywilling.cn.monitor.listener;

import com.skywilling.cn.common.model.BasicCarResponse;
import org.springframework.stereotype.Component;

@Component
public class BasicListener {

    public void init() {
    }

    public BasicCarResponse process(String vin, boolean result, String body) {
        return null;
    }

    public BasicCarResponse process(String vin, String body) {
        return null;
    }
}
