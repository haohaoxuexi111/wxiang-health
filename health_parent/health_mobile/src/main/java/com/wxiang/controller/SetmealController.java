package com.wxiang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wxiang.constant.MessageConstant;
import com.wxiang.entity.Result;
import com.wxiang.pojo.Setmeal;
import com.wxiang.service.SetMealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetMealService setMealService;

    @RequestMapping("/getSetmeal")
    public Result getAll(){
        try {
            List<Setmeal> list = setMealService.getAll();
            for (Setmeal setmeal : list){
                System.out.println(setmeal.getId());
            }
            return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS, list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);
        }
    }

    // 获取套餐信息（套餐信息，及其关联的检查组和检查项信息）
    @RequestMapping("/findById")
    public Result findById(int id){
        try {
            Setmeal setmeal = setMealService.findById(id);
            return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, setmeal);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

    // 根据id只获取套餐信息
    @RequestMapping("/findSetmealById")
    public Result findSetmealById(int id){
        try {
            Setmeal setmeal = setMealService.findSetmealById(id);
            return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, setmeal);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }
}
