package com.wxiang.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wxiang.dao.MemberDao;
import com.wxiang.pojo.Member;
import com.wxiang.service.MemberService;
import com.wxiang.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        String password = member.getPassword();
        if (password != null){
            // 如果不是手机验证码登录触发的自动注册，而是用户通过账号注册表单输入账号密码注册的账号
            // 需要使用md5将明文密码进行加密
            password = MD5Utils.md5(password);
            member.setPassword(password);
        }
        memberDao.add(member);
    }

    // 根据月份查询会员数量
    @Override
    public Map<String, Integer> findMemberCountByMonths(List<String> months) {

        /*for (String month : months){
            String date = month + ".28";
            Integer count = memberDao.findMemberCountBeforeDate(date);
            memberCount.add(count);
        }*/
        // memberCount = memberDao.findMemberCountByMonths();
        Map<String, Integer> map = new TreeMap<>();
        List<Map<String, Object>> mapList = memberDao.findMemberCountByMonths();
        // mapList = [{”months“：”2021.05","count":3},{”months“：”2021.06","count":4}]
        for (Map m : mapList){
            String key = (String) m.get("months");
            Integer value =  ((Long)m.get("count")).intValue();
            map.put(key, value);
            // {2021.06=1, 2021.05=6, 2021.04=4, 2021.03=5, 2021.02=4, 2021.01=4, 2021.12=2,
            //  2021.11=2, 2021.10=2, 2022.02=3, 2022.03=2, 2021.09=2, 2021.08=1, 2021.07=1}
            System.out.println(map.toString());
        }

        return map;
    }
}
