package com.skywilling.cn.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.skywilling.cn.common.enums.ResultType;
import com.skywilling.cn.common.model.BasicResponse;
import com.skywilling.cn.common.utils.PhoneFormatCheckUtils;
import com.skywilling.cn.common.utils.StringUtil;

import com.skywilling.cn.manager.user.common.VerifyCodeType;
import com.skywilling.cn.manager.user.common.exception.ExpireCodeException;
import com.skywilling.cn.manager.user.common.exception.UnknowAccountException;
import com.skywilling.cn.manager.user.core.JwtToken;
import com.skywilling.cn.manager.user.model.UserInfo;
import com.skywilling.cn.manager.user.model.UserPermission;
import com.skywilling.cn.manager.user.model.UserRole;
import com.skywilling.cn.manager.user.service.*;
import com.skywilling.cn.web.model.UserRegisterInfo;
import com.skywilling.cn.web.model.view.PageView;
import com.skywilling.cn.web.model.view.UserAddParam;
import com.skywilling.cn.web.model.view.UserUpdateParam;
import com.skywilling.cn.web.model.view.UserView;
import com.skywilling.cn.web.utils.SmsHelper;
import com.skywilling.cn.web.utils.ViewBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1")
public class UserController {

  private static Logger LOG = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private VerifyCodeService verifyCodeService;

  @Autowired
  private UserManager userManager;

  @Autowired
  private UserInfoService userInfoService;

  @Autowired
  private RoleService roleService;

  @Autowired
  private PermissionService permissionService;

  @RequestMapping(value = "/user/verifyCode", method = RequestMethod.GET)
  public BasicResponse sendVerifyCode(@RequestParam("phoneNumber") String phoneNumber,
                                      @RequestParam("type") int type) {

    boolean legal = PhoneFormatCheckUtils.isPhoneLegal(phoneNumber);
    if (!legal) {
      return BasicResponse.buildResponse(ResultType.FAILED, "非法手机号");
    }

    if (type != VerifyCodeType.REGISTER) {
      UserInfo userInfo = userInfoService.queryByPhone(phoneNumber);
      if (userInfo == null) {
        return BasicResponse.buildResponse(ResultType.FAILED, "未注册");
      }
    }

    String code = StringUtil.getVerificationCode();
    boolean b = false;
    switch (type) {
      case VerifyCodeType.REGISTER: {
        b = SmsHelper.sendRegisterVerifyCode(phoneNumber, code);
        break;
      }
      case VerifyCodeType.LOGIN: {
        b = SmsHelper.sendLoginVerifyCode(phoneNumber, code);
        break;
      }
      case VerifyCodeType.FORGET_PWD: {
        b = SmsHelper.sendForgetPasswordVerifyCode(phoneNumber, code);
        break;
      }
      default: {
        return BasicResponse.buildResponse(ResultType.FAILED, "type 类型不存在");
      }
    }
    if (b) {
      verifyCodeService.save(phoneNumber, code, type);
      return BasicResponse.buildResponse(ResultType.SUCCESS, null);
    }
    return BasicResponse.buildResponse(ResultType.FAILED, "验证码发送失败");
  }

  @RequestMapping(value = "/user/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse register(UserRegisterInfo registerInfo) {

    if (registerInfo == null || !registerInfo.getValidatePwd().contentEquals(registerInfo.getValidatePwd())) {
      return BasicResponse.buildResponse(ResultType.FAILED, "参数不能为空，且密码必须一致");
    }

    boolean legal = PhoneFormatCheckUtils.isPhoneLegal(registerInfo.getPhoneNumber());
    if (!legal) {
      return BasicResponse.buildResponse(ResultType.FAILED, "非法手机号");
    }

    try {
      boolean verify = verifyCodeService.verify(registerInfo.getPhoneNumber(), registerInfo.getVerifyCode(), VerifyCodeType.REGISTER);
      if (!verify)
        return BasicResponse.buildResponse(ResultType.FAILED, "验证码不正确");
    } catch (ExpireCodeException e) {
      e.printStackTrace();
      return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
    }

    UserInfo userInfo = new UserInfo();
    userInfo.setUsername(registerInfo.getUsername());
    userInfo.setPassword(registerInfo.getPassword());
    userInfo.setPhoneNumber(registerInfo.getPhoneNumber());
    UserRole guest = roleService.query("guest");
    userInfo.setRole(guest);
    try {
      userInfoService.save(userInfo);
      return BasicResponse.buildResponse(ResultType.SUCCESS, null);
    } catch (Exception e) {
      return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
    }
  }

  @RequestMapping(value = "/user/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse login(@RequestParam(value = "username", required = false) String username,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                             @RequestParam(value = "verifyCode", required = false) String verifyCode,
                             @RequestParam(value = "type", required = true) int type) {
    UserInfo userInfo = null;
    switch (type) {
      case 1: {
        try {
          userInfo = userManager.loginByName(username, password);
        } catch (UnknowAccountException | AuthenticationException e) {
          e.printStackTrace();
          return BasicResponse.buildResponse(ResultType.FAILED, "用户名或密码不正确");
        }
        break;
      }
      case 2: {
        try {
          userInfo = userManager.loginByPhone(phoneNumber, verifyCode);
        } catch (AuthenticationException e) {
          e.printStackTrace();
          return BasicResponse.buildResponse(ResultType.FAILED, "验证码输入错误");
        } catch (ExpireCodeException e) {
          e.printStackTrace();
          return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
        }
        break;
      }
      default: {
        return BasicResponse.buildResponse(ResultType.FAILED, "非法登陆");
      }
    }
    try {
      PageInfo<UserPermission> userPermissionPageInfo = permissionService.queryByRole(userInfo.getRole().getId(), 1, 0);
      List<String> permissions = userPermissionPageInfo.getList().stream().map(UserPermission::getPermission).collect(Collectors.toList());

      String token = JwtToken.createToken(userInfo.getUid());
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("token", token);
      jsonObject.put("privileges", permissions);
      return BasicResponse.buildResponse(ResultType.SUCCESS, jsonObject);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return BasicResponse.buildResponse(ResultType.FAILED, null);
    }
  }

  @RequestMapping(value = "/user/{uid}/logout", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse logout(@PathVariable("uid") Integer uid, HttpServletRequest request) {

    return BasicResponse.buildResponse(ResultType.SUCCESS, null);
  }

  @RequestMapping(value = "/user/password/change", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse passwordChange(@RequestParam("phoneNumber") String phoneNumber,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPwd") String confirmPwd,
                                      @RequestParam("verifyCode") String verifyCode) {

    boolean legal = PhoneFormatCheckUtils.isPhoneLegal(phoneNumber);
    if (!legal) {
      return BasicResponse.buildResponse(ResultType.FAILED, "非法手机号");
    }

    if (!password.equals(confirmPwd)) {
      return BasicResponse.buildResponse(ResultType.FAILED, "密码必须一致");
    }

    try {
      boolean verify = verifyCodeService.verify(phoneNumber, verifyCode, VerifyCodeType.FORGET_PWD);
      if (!verify) {
        return BasicResponse.buildResponse(ResultType.FAILED, "验证码错误");
      }
    } catch (ExpireCodeException e) {
      e.printStackTrace();
      return BasicResponse.buildResponse(ResultType.FAILED, e.getMessage());
    }

    UserInfo userInfo = userInfoService.queryByPhone(phoneNumber);
    if (userInfo == null) {
      return BasicResponse.buildResponse(ResultType.FAILED, "手机号码未注册");
    }

    userInfoService.updatePassword(userInfo.getUid(), password);
    return BasicResponse.buildResponse(ResultType.SUCCESS, null);
  }

  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public BasicResponse users(@RequestParam("page") int page,
                             @RequestParam("size") int size,
                             @RequestParam(value = "username", required = false) String username) {

    if (StringUtils.isEmpty(username)) {
      PageInfo<UserInfo> pageInfo = userInfoService.query(page, size);
      List<UserView> views = pageInfo.getList().stream().map(ViewBuilder::build).collect(Collectors.toList());
      PageView pageView = ViewBuilder.build(pageInfo);
      pageView.setList(views);
      return BasicResponse.buildResponse(ResultType.SUCCESS, pageView);
    } else {
      UserInfo userInfo = userInfoService.query(username);
      return BasicResponse.buildResponse(ResultType.SUCCESS, ViewBuilder.build(userInfo));
    }
  }

  /**
   * 根据Uid查询信息
   */
  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse userByQueryId(@PathVariable("userId") int userId) {
    try {
      UserInfo pageInfo = userInfoService.query(userId);
      return BasicResponse.buildResponse(ResultType.SUCCESS, ViewBuilder.build(pageInfo));
    } catch (Exception e) {
      return BasicResponse.buildResponse(ResultType.FAILED, e.getClass());
    }
  }
  /**
   * 根据phoneNumber查询信息
   */
  @RequestMapping(value = "/user/{phoneNumber}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse userByQueryPhone(@PathVariable("phoneNumber") int number){
    try {
      UserInfo pageInfo = userInfoService.query(number);
      return BasicResponse.buildResponse(ResultType.SUCCESS, ViewBuilder.build(pageInfo));
    } catch (Exception e) {
      return BasicResponse.buildResponse(ResultType.FAILED, e.getClass());
    }
  }

  //自动延时token
  @RequestMapping(value = "/token/refresh", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public BasicResponse refreshToken() {

    return BasicResponse.buildResponse(ResultType.FAILED, null);
  }

  //仅限超级管理员和用户本身可以修改，不能修改用户角色和状态
  @RequestMapping(value = "/user/{uid}/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public BasicResponse updateUser(@PathVariable("uid") Integer uid,
                                  @RequestBody UserUpdateParam updateParam,
                                  HttpServletRequest request) {
    try {
      UserInfo userInfo = updateParam.toUserInfo();
      userInfoService.update(userInfo);
      return BasicResponse.buildResponse(ResultType.SUCCESS, null);
    } catch (Exception e) {
      return BasicResponse.buildResponse(ResultType.FAILED, null);
    }
  }

  //以下的方法只能由超级管理员调用
  @RequestMapping(value = "/user/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public BasicResponse addUser(@RequestBody UserAddParam addParam) {
    UserInfo userInfo = addParam.toUserInfo();
    UserRole role = roleService.query(addParam.getRoleId());
    if (role == null) {
      role = roleService.query("guest");
    }
    userInfo.setRole(role);
    try {
      userInfoService.save(userInfo);
      return BasicResponse.buildResponse(ResultType.SUCCESS, userInfo);
    } catch (Exception e) {
      return BasicResponse.buildResponse(ResultType.FAILED, null);
    }
  }

  @RequestMapping(value = "/user/{userId}/role/{roleId}")
  public BasicResponse changeRole(@PathVariable("userId") int userId,
                                  @PathVariable("roleId") int roleId) {
    UserRole role = roleService.query(roleId);
    if (role == null) {
      return BasicResponse.buildResponse(ResultType.FAILED, "role is error");
    }
    UserInfo userInfo = new UserInfo();
    userInfo.setUid(userId);
    userInfo.setRole(role);
    userInfoService.update(userInfo);
    return BasicResponse.buildResponse(ResultType.SUCCESS, null);
  }

  @RequestMapping(value = "/user/{uid}/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse delete(@PathVariable("uid") Integer uid) {
    try {
      userInfoService.delete(uid);
      return BasicResponse.buildResponse(ResultType.SUCCESS, null);
    } catch (Exception e) {
      return BasicResponse.buildResponse(ResultType.FAILED, null);
    }
  }

  /**
   * 密码重置为“abc123”，用户可以通过password/change接口修改密码
   */
  @RequestMapping(value = "/user/{uid}/resetPwd", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public BasicResponse resetPwd(@PathVariable("uid") Integer uid) {
    try {
      userInfoService.updatePassword(uid,"abc123");
      return BasicResponse.buildResponse(ResultType.SUCCESS, null);
    } catch (Exception e) {
      return BasicResponse.buildResponse(ResultType.FAILED, e.getClass());
    }
  }


}
