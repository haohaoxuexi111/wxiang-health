package com.wxiang.jobs;

import java.util.Date;

/**
 * 测试quarz执行自定义job
 */
public class JobDemo {
    public void run(){
        System.out.println("自定义任务running---" + new Date());
    }
}
