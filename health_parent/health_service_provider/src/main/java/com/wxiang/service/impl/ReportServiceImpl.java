package com.wxiang.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wxiang.dao.MemberDao;
import com.wxiang.dao.OrderDao;
import com.wxiang.service.ReportService;
import com.wxiang.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营数据统计服务
 */
// 注意不要导错了包 是import com.alibaba.dubbo.config.annotation.Service;包，不是SpringFrame那个包
@Service(interfaceClass = ReportService.class)
@Transactional
public class ReportServiceImpl implements ReportService{

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    // 查询运营数据
    @Override
    public Map<String, Object> getBusinessReportData() throws Exception {
        Map<String, Object> map = new HashMap<>();

        String today = DateUtils.parseDate2String(DateUtils.getToday());
        // 报表生成日期
        map.put("reportDate", today);

        // 本日新增会员数
        Integer todayNewMember = memberDao.findMemberCountByDate(today);
        map.put("todayNewMember", todayNewMember);

        // 总会员数
        Integer totalMember = memberDao.findMemberTotalCount();
        map.put("totalMember", totalMember);

        // 本周新增会员数，从本周一到今天新增的会员数
        // 获得本周一的日期
        String monday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(monday);
        map.put("thisWeekNewMember", thisWeekNewMember);

        // 本月新增会员数
        // 获得本月第一天的日期
        String thisMonthFirstDay = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(thisMonthFirstDay);
        map.put("thisMonthNewMember", thisMonthNewMember);

        // 今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(today);
        map.put("todayOrderNumber", todayOrderNumber);

        // 今日到诊数
        Integer todayVisitsNumber = orderDao.findVisitsCountAfterDate(today);
        map.put("todayVisitsNumber", todayVisitsNumber);

        // 本周预约数
        Integer thisWeekOrderNumber = orderDao.findOrderCountAfterDate(monday);
        map.put("thisWeekOrderNumber", thisWeekOrderNumber);

        // 本周到诊数
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(monday);
        map.put("thisWeekVisitsNumber", thisWeekVisitsNumber);

        // 本月预约数
        Integer thisMonthOrderNumber = orderDao.findOrderCountAfterDate(thisMonthFirstDay);
        map.put("thisMonthOrderNumber", thisMonthOrderNumber);

        // 本月到诊数
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(thisMonthFirstDay);
        map.put("thisMonthVisitsNumber", thisMonthVisitsNumber);

        // 热门套餐查询
        List<Map> hotSetmeal = orderDao.findHotSetmeal();
        map.put("hotSetmeal", hotSetmeal);
        return map;
    }
}
