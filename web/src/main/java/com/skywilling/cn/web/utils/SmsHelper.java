package com.skywilling.cn.web.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class SmsHelper {

    static final String product="Dysmsapi";
    static final String domain = "dysmsapi.aliyuncs.com";

    static final String accessKeyId = "LTAIXejlRwUtc7NF";
    static final String accessKeySecret = "IlvHmCbIbPJXvCIINt66iTcS1e4D0j";

    static final String REGISTER_VERIFY_CODE = "SMS_108370006";
    static final String LOGIN_VERIFY_CODE = "SMS_108370006";
    static final String RESERVE_VERIFY_CODE = "SMS_126780378";
    static final String FORGET_PASSWORD_CODE = "SMS_108370006";



    /*
     * 发送注册验证码
     */
    public static boolean sendRegisterVerifyCode(String phoneNumber, String code) {
        return sendSms(phoneNumber, code, REGISTER_VERIFY_CODE);
    }

    /**
     * 发送登录验证验证码
     */
    public static boolean sendLoginVerifyCode(String phoneNumber, String code) {
        return sendSms(phoneNumber, code, LOGIN_VERIFY_CODE);
    }

    /**
     * 发送预约订单验证码
     */
    public static boolean sendReserveVerifyCode(String phoneNumber, String code) {
        return sendSms(phoneNumber, code, RESERVE_VERIFY_CODE);
    }

    /**
     * 发送找回密码验证码
     */
    public static boolean sendForgetPasswordVerifyCode(String phoneNumber, String code) {
        return sendSms(phoneNumber, code, FORGET_PASSWORD_CODE);
    }

    public static boolean sendSms(String phoneNumber, String code, String templateCode){

        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient=new DefaultAcsClient(profile);
            SendSmsRequest request=new SendSmsRequest();
            request.setPhoneNumbers(phoneNumber);
            request.setSignName("云乐用车");
            request.setTemplateCode(templateCode);
            request.setTemplateParam("{\"code\":\""+code+"\"}");
            SendSmsResponse sendSmsResponse=acsClient.getAcsResponse(request);
            if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                System.out.println("Send success ");
                return true;
            } else {
                System.out.println("Send failed " + sendSmsResponse.getCode());
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }
}
