package com.skywilling.cn.livemap.factory;

public interface Factory<T> {

  T create(String parkNanme, String url);
}
