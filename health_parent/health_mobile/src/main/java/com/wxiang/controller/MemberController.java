package com.wxiang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wxiang.constant.MessageConstant;
import com.wxiang.constant.RedisMessageConstant;
import com.wxiang.entity.Result;
import com.wxiang.pojo.Member;
import com.wxiang.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private MemberService memberService;
    // 手机号快速登录
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map map) throws IOException {
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        // 从Redis中获取验证码
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        if (validateCodeInRedis != null && validateCode != null && validateCode.equals(validateCodeInRedis)){
            // 验证码正确，查询会员信息是否存在
            Member member = memberService.findByTelephone(telephone);
            if (member == null){
                // 不是会员，自动注册。关于数据库时间有关的字段：
                // 建议设计为：`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间'
                member.setRegTime(new Date());
                member.setPhoneNumber(telephone);
                memberService.add(member);
            }
            // 向客户端浏览器写入Cookie，内容为手机号
            Cookie cookie = new Cookie("lobin_member_telephone", telephone);
            cookie.setPath("/");  // "/"表示只要是项目下的路径都会带cookie进行访问。
            cookie.setMaxAge(60*60*24*30); // 有效期30天
            response.addCookie(cookie);
            // 将会员信息转换为json字符串，并保存到Redis中
            String memberStr = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone, 60*30,memberStr);
            return new Result(true, MessageConstant.LOGIN_SUCCESS);
        }else {
            // 验证码错误
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }

}
