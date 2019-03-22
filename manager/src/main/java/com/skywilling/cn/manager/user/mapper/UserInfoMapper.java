package com.skywilling.cn.manager.user.mapper;


import com.skywilling.cn.manager.user.model.UserInfo;

import java.util.List;

public interface UserInfoMapper extends Mapper<UserInfo> {

  List<UserInfo> queryByRole(int roleId);

  UserInfo queryByPhone(String phoneNumber);

}
