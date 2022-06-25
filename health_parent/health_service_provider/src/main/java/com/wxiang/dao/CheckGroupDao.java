package com.wxiang.dao;

import com.github.pagehelper.Page;
import com.wxiang.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {
    public void add(CheckGroup checkGroup);
    public void addCheckGroupAndCheckItem(Map map);
    public Page<CheckGroup> findByCondition(String queryString);
    public CheckGroup findById(Integer id);
    public List<Integer> findItemIdsByGroupId(Integer id);
    public void edit(CheckGroup checkGroup);
    public void deleteAssociationItems(Integer id);
    public List<CheckGroup> findAll();
}
