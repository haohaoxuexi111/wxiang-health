package com.wxiang.constant;

public class RedisConstant {
    // 上传到服务器的所有套餐图片名称，不一定包含在数据库中，存在垃圾图片
    public static final String SETMEAL_PIC_RESOURCES = "setmealPicResources";
    // 保存在数据库中的套餐图片名称，用户新建套餐时确实提交了的套餐图片
    public static final String SETMEAL_PIC_DB_RESOURCES = "setmealDbResources";
}
