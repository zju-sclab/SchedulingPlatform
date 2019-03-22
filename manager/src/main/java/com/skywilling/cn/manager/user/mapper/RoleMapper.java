package com.skywilling.cn.manager.user.mapper;



import com.skywilling.cn.manager.user.model.UserRole;

import java.util.List;

public interface RoleMapper extends Mapper<UserRole> {

  List<UserRole> queryByPermission(int permissionId);
}
