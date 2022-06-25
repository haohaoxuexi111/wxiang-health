package com.wxiang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.wxiang.constant.MessageConstant;
import com.wxiang.constant.RedisMessageConstant;
import com.wxiang.entity.Result;
import com.wxiang.pojo.Order;
import com.wxiang.service.OrderService;
import com.wxiang.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * 体检预约处理
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private JedisPool jedisPool;
    @Reference  // import com.alibaba.dubbo.config.annotation.Reference;小心别引错了包
    private OrderService orderService;

    // 在线体检预约
    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map){
        String tele = (String) map.get("telephone");
        // 获取redis中保存的验证码，因为有三个地方要获取验证码，为了区分定义了RedisMessageConstant.SENDTYPE_ORDER
        String validateCodeInRedis = jedisPool.getResource().get(tele + RedisMessageConstant.SENDTYPE_ORDER);
        String validateCode = (String) map.get("validateCode");
        // 与用户输入的验证码比较
        if (validateCodeInRedis != null && validateCode != null && validateCode.equals(validateCodeInRedis)){
            map.put("orderType", Order.ORDERTYPE_WEIXIN); // 设置预约类型，微信预约
            Result result = new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
            try {
                System.out.println("OrderController: result=" + result);
                if (orderService != null) {
                    result = orderService.order(map);
                }
                if (orderService == null){
                    System.out.println("orderService is null!!!!!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
            if (result.isFlag()){
                // 预约成功，为用户发送短信，提示预约成功
                /*try {  // 预约成功，您预约的口令是xxxx
                    SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, tele, (String) map.get("orderDate"));
                } catch (ClientException e) {
                    e.printStackTrace();
                }*/
                System.out.println("预约成功!!");
            }
            return result;
        }else {
            System.out.println("验证码错误!!");
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }

    // 根据预约ID查询预约相关信息
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try{
            Map map = orderService.findById(id);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS, map);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
