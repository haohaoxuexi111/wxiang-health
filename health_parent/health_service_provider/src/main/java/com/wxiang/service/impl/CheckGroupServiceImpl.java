package com.wxiang.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxiang.dao.CheckGroupDao;
import com.wxiang.entity.PageResult;
import com.wxiang.entity.QueryPageBean;
import com.wxiang.pojo.CheckGroup;
import com.wxiang.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检查组服务
 */
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Resource
    private CheckGroupDao checkGroupDao;
    // 新增检查组，同时需要让检查组关联检查项
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        // 新增检查组，操作t_checkgroup表
        checkGroupDao.add(checkGroup);
        // 设置检查组和检查项的关联关系，操作t_checkgroup_checkitem表
        Integer checkGroupId = checkGroup.getId(); // keyProperty="id" 使用selectKey标签将执行完插入之后，自增的id值赋给checkgroup对象的id属性，因此这里可以使用getId得到id值
        this.addCheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }

    @Override // 分页(条件)查询
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage, pageSize);
        Page<CheckGroup> page = checkGroupDao.findByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        CheckGroup checkGroup = checkGroupDao.findById(id);
        return checkGroup;
    }

    @Override
    public List<Integer> findItemIdsByGroupId(Integer id) {
        return checkGroupDao.findItemIdsByGroupId(id);
    }

    // 保存编辑后的检查组和关联的检查项信息
    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        // 修改检查组信息 t_checkgroup
        checkGroupDao.edit(checkGroup);
        // 清理检查组关联的检查项 t_checkgroup_checkitem
        checkGroupDao.deleteAssociationItems(checkGroup.getId());
        // 重新关联检查组和检查项
        // 设置检查组和检查项的关联关系，操作t_checkgroup_checkitem表
        Integer checkGroupId = checkGroup.getId(); // keyProperty="id" 使用selectKey标签将执行完插入之后，自增的id值赋给checkgroup对象的id属性，因此这里可以使用getId得到id值
        this.addCheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    // 抽取重复代码，建立检查组和检查项多对多的关系
    public void addCheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkitemIds){
        if (checkitemIds != null &&checkitemIds.length > 0){
            Map<String, Integer> map = null;
            for (Integer checkitemId : checkitemIds) {
                map = new HashMap<>();
                map.put("checkgroupId", checkGroupId);  // 这里设置的key在mapper.xml文件中取值#{}时要用到
                map.put("checkitemId", checkitemId);
                checkGroupDao.addCheckGroupAndCheckItem(map);
            }
        }
    }
}
