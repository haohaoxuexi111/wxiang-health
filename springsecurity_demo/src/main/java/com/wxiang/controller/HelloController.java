package com.wxiang.controller;

import org.apache.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注解方式控制类中方法的调用，实现权限控制，工作常用
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    private static Logger logger = Logger.getLogger(HelloController.class);

    @RequestMapping("/add")  // 访问地址: http://localhost:84/hello/add.do
    @PreAuthorize("hasAuthority('add')")  // 调用此方法要求当前用户必须具有add权限
    public String add(){
        logger.info("=====进入add方法=====");
        return "success";
    }

    @RequestMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(){
        logger.info("=====进入delete方法=====");
        return "success";
    }
}
