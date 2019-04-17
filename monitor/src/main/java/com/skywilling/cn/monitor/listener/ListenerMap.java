package com.skywilling.cn.monitor.listener;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ListenerMap {

    private ConcurrentHashMap<String, BasicListener> listenerMap = new ConcurrentHashMap<>();

    public void addListener(String name, BasicListener listener) {
        this.listenerMap.putIfAbsent(name, listener);
    }

    public BasicListener getListener(String name) {
        return this.listenerMap.get(name);
    }
}
