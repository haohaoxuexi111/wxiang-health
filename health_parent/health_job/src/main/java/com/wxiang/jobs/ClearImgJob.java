package com.wxiang.jobs;

import com.wxiang.constant.RedisConstant;
import com.wxiang.utils.QiNiuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 自定义job，定时清理用户上传到七牛云的垃圾图片
 */
public class ClearImgJob {

    @Autowired
    private JedisPool jedisPool;

    public void clearImg(){
        // 根据Redis中保存的两个set集合，让两集合相减，可得垃圾图片集合
        Set<String> sDiff = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if (sDiff != null){
            for (String picName : sDiff){
                // 删除七牛云服务器上的图片
                QiNiuUtils.deleteFileFromQiNiu(picName);
                // 从redis集合中删除图片名称
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES, picName);
                System.out.println("清理垃圾图片：" + picName);
            }
        }

    }


}
