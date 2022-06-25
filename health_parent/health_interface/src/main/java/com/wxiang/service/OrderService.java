package com.wxiang.service;

import com.wxiang.entity.Result;

import java.util.Map;

public interface OrderService {
    public Result order(Map map) throws Exception;

    Map findById(Integer id);
}
