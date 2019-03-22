package com.skywilling.cn.manager.user.service;

import com.github.pagehelper.PageInfo;
import com.skywilling.cn.manager.user.model.UserInfo;


public interface UserInfoService extends Service<UserInfo>{

  void updatePassword(int uid, String password);

  PageInfo<UserInfo> queryByRole(int roleId, int page, int size);

  UserInfo queryByPhone(String phoneNumber);

}
