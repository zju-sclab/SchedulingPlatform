package com.skywilling.cn.manager.user.service;

import com.skywilling.cn.manager.user.common.exception.ExpireCodeException;

public interface VerifyCodeService {

 void save(String phoneNumber, String code, int type);

 boolean verify(String phoneNumber, String code, int type) throws ExpireCodeException;

}
