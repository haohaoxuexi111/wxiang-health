package com.wxiang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wxiang.constant.MessageConstant;
import com.wxiang.constant.RedisConstant;
import com.wxiang.entity.PageResult;
import com.wxiang.entity.QueryPageBean;
import com.wxiang.entity.Result;
import com.wxiang.pojo.Setmeal;
import com.wxiang.service.SetMealService;
import com.wxiang.utils.QiNiuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

/**
 * 体检套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetMealController {

    // 使用JedisPool操作Redis服务
    @Autowired
    private JedisPool jedisPool;

    // 文件上传，需要在springmvc.xml中配置组件CommonsMultipartResolver
    @RequestMapping("/upload") // 绑定前端<el-upload>标签内指定的name="imgFile"，相当于为图片指定的对象名，这里名字相同可以不用绑定
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile) {
        System.out.println(imgFile);
        // 获得文件的原始文件名，即上传时的文件名
        String originalFilename = imgFile.getOriginalFilename();
        // 获取文件后缀
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); // .jpg
        String fileName = UUID.randomUUID().toString() + extention;
        try {
            // 将文件上传到七牛云服务器
            QiNiuUtils.upload2QiNiu(imgFile.getBytes(),fileName);
            // 将文件名存到Redis的set集合中
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, fileName);
    }

    @Reference
    private SetMealService setMealService;

    // 新增套餐
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){
        try {
            setMealService.add(setmeal,checkgroupIds);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    // 分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        return setMealService.pageQuery(queryPageBean);
    }
}
