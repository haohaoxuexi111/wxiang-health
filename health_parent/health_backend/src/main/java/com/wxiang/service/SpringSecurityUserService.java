package com.wxiang.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wxiang.pojo.Permission;
import com.wxiang.pojo.Role;
import com.wxiang.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {

    // 使用dubbo远程调用服务，查询数据库获取用户信息；
    // 要想引入UserService服务，必须让dubbo扫描到这个类，
    // 所以扩大了<dubbo:annotation package="com.wxiang" />配置的范围
    @Reference
    private UserService userService;

    // 根据用户名查询数据库，获取用户信息；这个方法由框架调用
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        System.out.println(user.toString() + "授权");
        if (user == null){
            // 用户不存在
            System.out.println("用户不存在");
            return null;
        }
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

        // 动态为当前用户授权，User中有属性：Set<Role> roles；而Role中有属性Set<Permission> permissions 和 LinkedHashSet<Menu> menus
        Set<Role> roles = user.getRoles();
        if (roles != null){
            for (Role role : roles) {
                // 遍历角色集合，并将角色授予用户
                list.add(new SimpleGrantedAuthority(role.getKeyword()));
                Set<Permission> permissions = role.getPermissions();
                if (permissions != null) {
                    for (Permission permission : permissions) {
                        // 遍历权限集合，并将权限授予用户
                        list.add(new SimpleGrantedAuthority(permission.getKeyword()));

                    }
                }else {
                    System.out.println(user.getUsername() + " 拥有的 权限 为 null");
                }
            }
            list.forEach(System.out::println);
        }else {
            System.out.println(user.getUsername() + " 拥有的 角色 为 null");
        }

        // 将用户信息返回给SpringSecurity框架，框架会进行密文密码的比对
        org.springframework.security.core.userdetails.User  securityUser = new org.springframework.security.core.userdetails.User(username, user.getPassword(), list);
        return securityUser;
    }
}
