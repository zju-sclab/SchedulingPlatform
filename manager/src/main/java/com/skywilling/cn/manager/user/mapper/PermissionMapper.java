package com.skywilling.cn.manager.user.mapper;


import com.skywilling.cn.manager.user.model.UserPermission;

import java.util.List;


public interface PermissionMapper extends Mapper<UserPermission> {

  List<UserPermission> queryByRole(int roleId);
}
