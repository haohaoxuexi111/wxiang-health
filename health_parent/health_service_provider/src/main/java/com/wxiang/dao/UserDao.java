package com.wxiang.dao;

import com.wxiang.pojo.User;

public interface UserDao {
    public User findByUsername(String username);
}
