package com.wxiang.service;

import com.wxiang.entity.PageResult;
import com.wxiang.entity.QueryPageBean;
import com.wxiang.entity.Result;
import com.wxiang.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService {
    // 新增检查组
    public void add(CheckGroup checkGroup, Integer[] checkitemIds);
    // 分页查询
    public PageResult pageQuery(QueryPageBean queryPageBean);

    public CheckGroup findById(Integer id);

    public List<Integer> findItemIdsByGroupId(Integer id);

    public void edit(CheckGroup checkGroup, Integer[] checkitemIds);

    public List<CheckGroup> findAll();
}
