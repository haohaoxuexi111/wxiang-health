package com.wxiang.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wxiang.dao.OrderSettingDao;
import com.wxiang.pojo.OrderSetting;
import com.wxiang.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约设置服务
 */
@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    OrderSettingDao orderSettingDao;

    @Override // 批量导入预约数据
    public void add(List<OrderSetting> orderData) {
        if (orderData != null && orderData.size() > 0){
            for(OrderSetting orderSetting : orderData){
                // 判断当前日期是否已经进行了预约设置
                long countByOrderDate = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                System.out.println(countByOrderDate);
                System.out.println(orderSetting.getOrderDate());
                if (countByOrderDate > 0){
                    // 已经进行了预约设置，执行更新操作
                    System.out.println("countByOrderDate > 0");
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else {
                    // 该日期没有预约设置，执行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }else {
            System.out.println("OrderSettingServiceImpl orderData is null");
        }
    }

    // 根据月份查询对应的预约设置数据
    @Override
    public List<Map> getOrderSettingByMonth(String date) {  // yyyy-MM
        String begin = date + "-01";
        String end = date + "-31";
        Map<String, String> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        List<Map> result = new ArrayList<>();
        if (list != null){
            for (OrderSetting orderSetting : list){
                Map<String, Object> m = new HashMap<>();
                m.put("date", orderSetting.getOrderDate().getDate());
                m.put("number", orderSetting.getNumber());
                m.put("reservations", orderSetting.getReservations());
                result.add(m);
            }
        }else {
            System.out.println("orderSettingDao.getOrderSettingByMonth(map) return null");
        }
        return result;
    }

    // 根据日期修改可预约人数
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if (count > 0){
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else {
            orderSettingDao.add(orderSetting);
        }
    }
}
