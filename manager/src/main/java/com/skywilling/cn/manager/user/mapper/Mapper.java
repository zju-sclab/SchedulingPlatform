package com.skywilling.cn.manager.user.mapper;

import java.util.List;

public interface Mapper<T> {

  void save(T t);

  void delete(Integer id);

  void update(T t);

  T query(Integer id);

  T queryBy(String name);

  List<T> queryAll();
}
