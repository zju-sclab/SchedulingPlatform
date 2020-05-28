package com.skywilling.cn.manager.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.skywilling.cn.manager.user.mapper.RoleAndPermissionMapper;
import com.skywilling.cn.manager.user.mapper.RoleMapper;
import com.skywilling.cn.manager.user.model.RoleAndPermission;
import com.skywilling.cn.manager.user.model.UserRole;
import com.skywilling.cn.manager.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleMapper roleMapper;

  @Autowired
  private RoleAndPermissionMapper roleAndPermissionMapper;

  @Transactional
  @Override
  public void save(UserRole userRole) {
    roleMapper.save(userRole);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    roleMapper.delete(id);
  }

  @Transactional
  @Override
  public void update(UserRole userRole) {
    roleMapper.update(userRole);
  }

  @Override
  public UserRole query(Integer id) {
    return roleMapper.query(id);
  }

  @Override
  public UserRole query(String name) {
    return roleMapper.queryBy(name);
  }

  @Override
  public PageInfo<UserRole> query(int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> roleMapper.queryAll());
  }

  @Transactional
  @Override
  public void addPermissions(int roleId, int[] permissionIds) {
    List<RoleAndPermission> list = Arrays.stream(permissionIds)
        .mapToObj((id) -> new RoleAndPermission(roleId, id)).collect(Collectors.toList());
    roleAndPermissionMapper.save(list);
  }

  @Transactional
  @Override
  public void deletePermission(int roleId, int permissionId) {
    RoleAndPermission roleAndPermission = new RoleAndPermission(roleId, permissionId);
    roleAndPermissionMapper.delete(roleAndPermission);
  }

  @Override
  public PageInfo<UserRole> queryByPermission(int permissionId, int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(()-> roleMapper.queryByPermission(permissionId));
  }
}
