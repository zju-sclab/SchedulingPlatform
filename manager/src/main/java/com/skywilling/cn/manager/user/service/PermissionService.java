package com.skywilling.cn.manager.user.service;

import com.github.pagehelper.PageInfo;
import com.skywilling.cn.manager.user.model.UserPermission;

public interface PermissionService extends Service<UserPermission> {

  PageInfo<UserPermission> queryByRole(int roleId, int page, int size);
}
