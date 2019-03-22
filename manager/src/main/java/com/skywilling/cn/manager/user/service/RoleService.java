package com.skywilling.cn.manager.user.service;

import com.github.pagehelper.PageInfo;
import com.skywilling.cn.manager.user.model.UserRole;

public interface RoleService extends Service<UserRole> {

  void addPermissions(int roleId, int[] permissionIds);

  void deletePermission(int roleId, int permissionId);

  PageInfo<UserRole> queryByPermission(int permissionId, int page, int size);

}
