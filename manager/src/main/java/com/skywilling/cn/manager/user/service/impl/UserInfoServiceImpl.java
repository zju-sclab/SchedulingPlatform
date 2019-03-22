package com.skywilling.cn.manager.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.skywilling.cn.common.utils.StringUtil;
import com.skywilling.cn.manager.user.core.PasswordService;
import com.skywilling.cn.manager.user.mapper.UserInfoMapper;
import com.skywilling.cn.manager.user.model.UserInfo;
import com.skywilling.cn.manager.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInfoServiceImpl implements UserInfoService {

  @Autowired
  private UserInfoMapper userInfoMapper;

  @Transactional
  @Override
  public void save(UserInfo userInfo) {
    String randomString = StringUtil.getRandomString(6);
    String pwd = PasswordService.encrypt(userInfo.getPassword(), randomString);
    userInfo.setPassword(pwd);
    userInfo.setPwdKey(randomString);

    userInfoMapper.save(userInfo);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    userInfoMapper.delete(id);
  }

  @Transactional
  @Override
  public void update(UserInfo userInfo) {
    userInfo.setPassword(null);
    userInfo.setPwdKey(null);
    userInfoMapper.update(userInfo);
  }

  @Override
  public void updatePassword(int uid, String password) {
    UserInfo userInfo = new UserInfo();
    userInfo.setUid(uid);
    String randomString = StringUtil.getRandomString(6);
    String pwd = PasswordService.encrypt(password, randomString);
    userInfo.setPassword(pwd);
    userInfo.setPwdKey(randomString);
    userInfoMapper.update(userInfo);
  }

  @Override
  public UserInfo query(Integer id) {
    return userInfoMapper.query(id);
  }

  @Override
  public UserInfo query(String name) {
    return userInfoMapper.queryBy(name);
  }

  @Override
  public PageInfo<UserInfo> query(int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> userInfoMapper.queryAll());
  }

  @Override
  public PageInfo<UserInfo> queryByRole(int roleId, int page, int size) {
    return PageHelper.startPage(page, size).doSelectPageInfo(() -> userInfoMapper.queryByRole(roleId));
  }

  @Override
  public UserInfo queryByPhone(String phoneNumber) {
    return userInfoMapper.queryByPhone(phoneNumber);
  }
}
