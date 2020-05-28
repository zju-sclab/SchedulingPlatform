package com.skywilling.cn.manager.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.skywilling.cn.manager.user.mapper.PermissionMapper;
import com.skywilling.cn.manager.user.model.UserPermission;
import com.skywilling.cn.manager.user.service.PermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionServiceImpl implements PermissionService {

  @Autowired
  private PermissionMapper permissionMapper;

  @Transactional
  @Override
  public void save(UserPermission permission) {
    permissionMapper.save(permission);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    permissionMapper.delete(id);
  }

  @Transactional
  @Override
  public void update(UserPermission permission) {
    permissionMapper.update(permission);
  }

  @Override
  public UserPermission query(Integer id) {
    return permissionMapper.query(id);
  }

  @Override
  public UserPermission query(String name) {
    return permissionMapper.queryBy(name);
  }

  @Override
  public PageInfo<UserPermission> query(int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> permissionMapper.queryAll());
  }

  @Override
  public PageInfo<UserPermission> queryByRole(int roleId, int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> permissionMapper.queryByRole(roleId));
  }
}
