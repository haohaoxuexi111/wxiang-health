package com.wxiang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wxiang.constant.MessageConstant;
import com.wxiang.entity.PageResult;
import com.wxiang.entity.QueryPageBean;
import com.wxiang.entity.Result;
import com.wxiang.pojo.CheckItem;
import com.wxiang.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkitem")
public class CheckItemController {

    // 查找服务
    @Reference
    private CheckItemService checkItemService;

    // 新增检查项
    @RequestMapping("/add") // @RequestBody完成json字符串到javabean之间的映射
    public Result add(@RequestBody CheckItem checkItem){
        System.out.println("come in controller");
        System.out.println(checkItem.toString());
        try {
            checkItemService.add(checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            // 服务调用失败
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
        return new Result(true,MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    // 分页条件查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = checkItemService.pageQuery(queryPageBean);
        return pageResult;  // 返回json字符串到页面
    }

    // 删除检查项
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")  // 权限校验
    @RequestMapping("/deleteById")
    public Result deleteById(Integer id){
        try {
            checkItemService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }

    // 根据id查询检查项
    @RequestMapping("/findById")
    public Result findById(Integer id){
        CheckItem checkItem = null;
        try {
            checkItem = checkItemService.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, checkItem);
    }

    @PostMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem){
        try {
            checkItemService.edit(checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }

    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<CheckItem> list = checkItemService.findAll();
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

}
