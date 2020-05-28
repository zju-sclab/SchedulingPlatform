package com.skywilling.cn.manager.user.mapper;


import com.skywilling.cn.manager.user.model.RoleAndPermission;

import java.util.List;

public interface RoleAndPermissionMapper {

  void save(List<RoleAndPermission> roleAndPermissions);

  void delete(RoleAndPermission roleAndPermission);
}
