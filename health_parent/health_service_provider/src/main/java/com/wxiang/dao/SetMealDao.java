package com.wxiang.dao;

import com.github.pagehelper.Page;
import com.wxiang.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetMealDao {
    public void add(Setmeal setmeal);
    public void addSetmealAndGroups(Map map);
    public Page<Setmeal> findByCondition(String queryString);
    public List<Setmeal> getAll();

    public Setmeal findById(int id);

    public Setmeal findSetmealById(int id);

    public List<Map<String, Object>> findSetmealCount();
}
