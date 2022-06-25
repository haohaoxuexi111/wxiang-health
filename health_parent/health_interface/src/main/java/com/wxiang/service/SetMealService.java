package com.wxiang.service;

import com.wxiang.entity.PageResult;
import com.wxiang.entity.QueryPageBean;
import com.wxiang.pojo.Setmeal;

import java.util.List;
import java.util.Map;

/**
 * 体检套餐服务接口
 */
public interface SetMealService {
    public void add(Setmeal setmeal, Integer[] checkgroupIds);
    public PageResult pageQuery(QueryPageBean queryPageBean);
    public List<Setmeal> getAll();
    public Setmeal findById(int id);

    public Setmeal findSetmealById(int id);

    List<Map<String, Object>> findSetmealCount();
}
