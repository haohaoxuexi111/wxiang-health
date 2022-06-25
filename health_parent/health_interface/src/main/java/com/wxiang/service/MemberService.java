package com.wxiang.service;

import com.wxiang.pojo.Member;

import java.util.List;
import java.util.Map;

public interface MemberService {
    // 根据手机号查询会员信息
    public Member findByTelephone(String telephone);
    public void add(Member member);
    public Map<String, Integer> findMemberCountByMonths(List<String> months);  //
}
