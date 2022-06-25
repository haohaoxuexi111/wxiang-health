package com.wxiang.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wxiang.constant.MessageConstant;
import com.wxiang.dao.MemberDao;
import com.wxiang.dao.OrderDao;
import com.wxiang.dao.OrderSettingDao;
import com.wxiang.entity.Result;
import com.wxiang.pojo.Member;
import com.wxiang.pojo.Order;
import com.wxiang.pojo.OrderSetting;
import com.wxiang.service.OrderService;
import com.wxiang.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    public Result order(Map map) throws Exception{
        System.out.println("hello Impl");
        // 1、检查用户所选择的预约日期是否已经提前进行了预约设置，如果没有设置则无法进行预约
        String orderDate = (String) map.get("orderDate");// 预约日期
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(DateUtils.parseString2Date(orderDate));
        if (orderSetting == null){
            // 指定日期未进行预约设置，无法进行预约
            System.out.println("OrderServiceImpl: orderSetting is null");
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }

        // 2、检查用户所选择的预约日期是否已经预约满，如果已经预约满则无法进行预约
        int number = orderSetting.getNumber(); // 可预约人数
        int reservations = orderSetting.getReservations(); // 已预约人数
        if (reservations >= number){
            // 预约人数已满，无法预约
            return new Result(false, MessageConstant.ORDER_FULL);
        }

        // 3、检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果重复预约则无法完成再次预约
        String tele = (String) map.get("telephone");
        Member member = memberDao.findByTelephone(tele);  // 根据用户电话号码判断是否要注册用户
        if (member != null){
            Integer memberId = member.getId();
            Date date = DateUtils.parseString2Date(orderDate);
            String setmealId = (String) map.get("setmealId");
            Order order = null;
            if (setmealId != null) {
                order = new Order(memberId, date, Integer.parseInt(setmealId));
            }else {
                System.out.println("OrderServiceImpl: setmealId is null!!!!");
            }
            System.out.println(order.toString());
            // 根据条件查询是否重复预约
            List<Order> list = orderDao.findByCondition(order);
            if (list != null && list.size() > 0){
                // 说明用户在重复预约
                return new Result(false, MessageConstant.HAS_ORDERED);
            }
        }else {
            // 4、检查当前用户是否为会员，如果不是会员则自动注册并进行预约
            member = new Member();
            member.setName((String) map.get("name"));
            member.setIdCard((String) map.get("idCard"));
            member.setPhoneNumber(tele);
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);
        }
        // 存在并发问题，可以使用分布式锁(Redis实现，zookeeper实现)解决
        // 5、预约成功，更新当日的已预约人数
        Order order = new Order();
        order.setMemberId(member.getId()); // 设置会员id
        order.setOrderDate(DateUtils.parseString2Date(orderDate)); // 预约日期
        order.setOrderType((String) map.get("orderType"));  // 预约方式
        order.setOrderStatus(Order.ORDERSTATUS_NO);  // 到诊状况
        order.setSetmealId(Integer.parseInt((String) map.get("setmealId")) ); // 套餐idInteger.parseInt((String) map.get("setmealId"))
        orderDao.add(order);
        orderSetting.setReservations(orderSetting.getReservations() + 1);
        orderSettingDao.editReservationByOrderDate(orderSetting);
        return new Result(true,MessageConstant.ORDER_SUCCESS, order.getId());
    }

    // 根据预约ID查询预约相关信息（预约人姓名，预约日期，套餐名称，预约类型）
    @Override
    public Map findById(Integer id) {
        Map map = orderDao.findById(id);
        if (map != null){
            // 处理预约日期的格式
            Date orderDate = (Date) map.get("orderDate");
            try {
                map.put("orderDate", DateUtils.parseDate2String(orderDate));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
