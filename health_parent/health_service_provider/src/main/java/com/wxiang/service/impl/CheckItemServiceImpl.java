package com.wxiang.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxiang.dao.CheckItemDao;
import com.wxiang.entity.PageResult;
import com.wxiang.entity.QueryPageBean;
import com.wxiang.entity.Result;
import com.wxiang.pojo.CheckItem;
import com.wxiang.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

// 注意引入的包：com.alibaba.dubbo.config.annotation.Service
// 配置了事务注解@Transactional之后，需要使用interfaceClass指定暴露的接口是CheckItemService，而不是事务的代理对象
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {
    // 注入dao对象
    @Autowired
    private CheckItemDao checkItemDao;
    @Override
    public void add(CheckItem checkItem) {
        System.out.println("come in CheckItemService");
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        // 使用基于Mybatis提供的分页助手插件PageHelper完成分页查询
        PageHelper.startPage(currentPage,pageSize);  // 拦截sql语句，并自动拼接到sql语句的后面
        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
        long total = page.getTotal();
        List<CheckItem> rows = page.getResult();
        return new PageResult(total, rows);  // 需要在service层返回PageResult对象
    }

    @Override
    public void deleteById(Integer id) {
        // 判断要删除的检查项是否与某个检查组关联
        long count = checkItemDao.findCountByCheckItemId(id);
        if (count > 0){
            // 当前检查项已经关联到检查组，不能删除
            throw new RuntimeException("当前检查项已存在于检查组内，无法删除");
        }
        checkItemDao.deleteById(id);
    }

    @Override
    public CheckItem findById(Integer id) {
        CheckItem checkItem = checkItemDao.findById(id);
        return checkItem;
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }

}
