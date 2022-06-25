package com.wxiang.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wxiang.dao.PermissionDao;
import com.wxiang.dao.RoleDao;
import com.wxiang.dao.UserDao;
import com.wxiang.pojo.Permission;
import com.wxiang.pojo.Role;
import com.wxiang.pojo.User;
import com.wxiang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    @Override // 根据用户名查询用户信息、角色信息、角色对应的权限信息
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);
        if (user == null){
            return null;
        }
        // 根据用户id查询对应的角色
        Set<Role> roles = roleDao.findByUserId(user.getId());
        for (Role role : roles) {
            // 根据角色id查询对应的权限
            Set<Permission> permissions = permissionDao.findByRoleId(role.getId());
            role.setPermissions(permissions);
        }
        user.setRoles(roles);
        return user;
    }
}
