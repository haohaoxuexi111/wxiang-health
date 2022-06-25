package com.wxiang.service;

import com.wxiang.pojo.User;

public interface UserService {
    public User findByUsername(String username);
}
