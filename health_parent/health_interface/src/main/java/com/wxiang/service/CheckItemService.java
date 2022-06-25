package com.wxiang.service;

import com.wxiang.entity.PageResult;
import com.wxiang.entity.QueryPageBean;
import com.wxiang.entity.Result;
import com.wxiang.pojo.CheckItem;

import java.util.List;

// 服务接口
public interface CheckItemService {
    public void add(CheckItem checkItem);
    public PageResult pageQuery(QueryPageBean queryPageBean);
    public void deleteById(Integer id);
    public CheckItem findById(Integer id);
    public void edit(CheckItem checkItem);
    public List<CheckItem> findAll();
}
