package com.wxiang.service;

import com.wxiang.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 要从数据库中获取用户的登录信息必须实现 UserDetailsService 接口
public class SpringSecurityUserService implements UserDetailsService {

    // 根据用户名查询用户信息
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("用户输入的用户名为：" + username);
        // 根据用户名查询数据库，获得用户信息
        // 将用户信息返回给security框架，框架会进行密码比对（比对用户输入的密码与数据库中的密码）
        User user = map.get(username); // 使用模拟数据
        if (user == null){
            // 用户名不存在
            return null;  // 登录校验不通过
        }else {
            // 将用户信息返回给security框架，框架会进行密码比对（比对用户输入的密码与数据库中的密码）
            List<GrantedAuthority> list = new ArrayList<>();
            // 为当前登录用户授权，后期需要从数据库中查询权限
            list.add(new SimpleGrantedAuthority("permission_A"));  // 授权
            list.add(new SimpleGrantedAuthority("permission_B"));  // 授权
            if (username.equals("wxiang")){
                list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));    // 授予角色
            }
            org.springframework.security.core.userdetails.User securityUser =
                    new org.springframework.security.core.userdetails.User(username, "{noop}"+user.getPassword(), list); // 使用明文密码
            return securityUser;
        }
    }

    // 模拟数据库中的用户数据
    public static Map<String, User> map = new HashMap<>();
    static{
        User user1 = new User();
        user1.setUsername("admin");
        user1.setPassword("1234");

        User user2 = new User();
        user2.setUsername("wxiang");
        user2.setPassword("1111");

        map.put(user1.getUsername(), user1);
        map.put(user2.getUsername(), user2);
    }

}
