package com.skywilling.cn.manager.user.service;

import com.github.pagehelper.PageInfo;

public interface Service<T> {
  void save(T t);

  void delete(Integer id);

  void update(T t);

  T query(Integer id);

  T query(String name);

  PageInfo<T> query(int page, int size);
}
