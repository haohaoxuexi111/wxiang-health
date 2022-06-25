package com.wxiang.dao;

import com.github.pagehelper.Page;
import com.wxiang.entity.PageResult;
import com.wxiang.entity.QueryPageBean;
import com.wxiang.entity.Result;
import com.wxiang.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {
    // 新增检查项
    public void add(CheckItem checkItem);
    // 分页条件查询
    public Page<CheckItem> selectByCondition(String queryString);
    // 判断检查项是否与某个检查组关联
    public long findCountByCheckItemId(Integer id);
    // 删除检查项
    public void deleteById(Integer id);
    // 根据id查询检查项
    public CheckItem findById(Integer id);
    // 编辑检查项
    public void edit(CheckItem checkItem);

    public List<CheckItem> findAll();
}
