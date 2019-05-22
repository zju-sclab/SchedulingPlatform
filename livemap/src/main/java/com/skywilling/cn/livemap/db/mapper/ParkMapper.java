package com.skywilling.cn.livemap.db.mapper;


import com.skywilling.cn.livemap.model.Park;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ParkMapper {

  void save(Park park);

  void delete(int id);

  void update(Park park);

  Park queryById(int id);

  Park queryByName(String name);

  List<Park> query();
}
