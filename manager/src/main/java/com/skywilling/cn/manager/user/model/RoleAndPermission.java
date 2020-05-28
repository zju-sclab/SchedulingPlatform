package com.skywilling.cn.manager.user.model;

import org.springframework.stereotype.Repository;

import java.io.Serializable;
@Repository
public class RoleAndPermission implements Serializable {
  private static final long serialVersionUID = 7902577677899533426L;
  private Integer roleId;
  private Integer permissionId;

  public RoleAndPermission() {
  }

  public RoleAndPermission(Integer roleId, Integer permissionId) {
    this.roleId = roleId;
    this.permissionId = permissionId;
  }

  public Integer getRoleId() {
    return roleId;
  }

  public void setRoleId(Integer roleId) {
    this.roleId = roleId;
  }

  public Integer getPermissionId() {
    return permissionId;
  }

  public void setPermissionId(Integer permissionId) {
    this.permissionId = permissionId;
  }
}
