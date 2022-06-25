package com.wxiang.controller;

import com.wxiang.constant.MessageConstant;
import com.wxiang.constant.RedisConstant;
import com.wxiang.constant.RedisMessageConstant;
import com.wxiang.entity.Result;
import com.wxiang.utils.SMSUtils;
import com.wxiang.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    private JedisPool jedisPool;

    // 用户体检预约发送验证码
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){
        // 随机生成4位数字的验证码
        // Integer validateCode = ValidateCodeUtils.generateValidateCode(4);
        // 给用户发送验证码，数字签名审核不通过，开发阶段使用固定验证码"1111"
        /*try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone,"1111".toString());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("验证码发送失败!");
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }*/
        // 将验证码保存到redis（5分钟后过期） setex(key, seconds, value)
        jedisPool.getResource().setex(telephone + RedisMessageConstant.SENDTYPE_ORDER, 300, "1111".toString());
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    // 用户手机快速登录发送验证码
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone){
        // 随机生成6位数字的验证码
        // Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        // 给用户发送验证码，数字签名审核不通过，开发阶段使用固定验证码"111111"
        /*try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone,"111111".toString());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("验证码发送失败!");
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }*/
        // 将验证码保存到redis（5分钟后过期） setex(key, seconds, value)
        jedisPool.getResource().setex(telephone + RedisMessageConstant.SENDTYPE_LOGIN, 300, "111111".toString());
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
