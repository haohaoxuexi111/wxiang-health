package com.wxiang.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxiang.constant.RedisConstant;
import com.wxiang.dao.SetMealDao;
import com.wxiang.entity.PageResult;
import com.wxiang.entity.QueryPageBean;
import com.wxiang.pojo.Setmeal;
import com.wxiang.service.SetMealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetMealService.class)
@Transactional
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealDao setMealDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${out_put_path}")
    private String outPutPath;  // 从属性文件中读取要生成的静态html对应的目录

    // 新增套餐信息，同时需要关联检查组
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setMealDao.add(setmeal);
        Integer setmealId = setmeal.getId();
        this.addSetmealAndCheckgroup(setmealId,checkgroupIds);
        // 将已经保存到数据库中的图片名称保存到Redis集合中
        String fileName = setmeal.getImg();
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, fileName);

        // 添加新的套餐后需要重新生成静态页面（套餐列表页面、套餐详情页面）
        generateMobileStaticHtml();
    }

    // 生成当前方法所需的静态页面  对于静态页面，重复生成会覆盖同名文件
    public void generateMobileStaticHtml(){
        // 在生成静态页面之前需要查询数据
        List<Setmeal> list = setMealDao.getAll();
        // 生成套餐列表静态页面
        generateMobileSetmealListHtml(list);
        // 生成套餐详情静态页面
        generateMobileSetmealDetailHtml(list);
    }

    // 生成套餐列表静态页面
    public void generateMobileSetmealListHtml(List<Setmeal> list){
        Map map = new HashMap();
        // 为模板提供数据，用于生成静态页面
        map.put("setmealList", list);  // 与ftl模板文件mobile_setmeal.ftl中的setmealList对应
        // 参数说明：templateName指定src/main/webapp/WEB-INF/ftl/中的模板文件；m_setmeal.html指定生成的静态页面的名称；map传递模板文件要填充的数据
        generateHtml("mobile_setmeal.ftl","m_setmeal.html",map); // 模板名要对应
    }

    // 生成套餐详情静态页面（可能有多个）
    public void generateMobileSetmealDetailHtml(List<Setmeal> list){
        for (Setmeal setmeal : list){
            Map map = new HashMap();
            map.put("setmeal", setMealDao.findById(setmeal.getId())); // setmeal与套餐详情模板文件对应
            // 为每个套餐生成套餐详情静态页面
            generateHtml("mobile_setmeal_detail.ftl","setmeal_detail_" + setmeal.getId() + ".html",map);
        }
    }

    // 生成静态页面通用的方法
    public void generateHtml(String templateName, String htmlPageName, Map map){
        // 获得配置对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        // 静态文件输出流
        Writer out = null;
        try {
            Template template = configuration.getTemplate(templateName);
            // 构造静态页面输出流
            out = new FileWriter(new File(outPutPath) + "/" + htmlPageName);
            // 输出文件
            template.process(map, out);
            out.close();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> page = setMealDao.findByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Setmeal> getAll() {
        System.out.println("come in service impl");
        List<Setmeal> list = setMealDao.getAll();
        return list;
    }

    @Override
    public Setmeal findById(int id) {
        return setMealDao.findById(id);
    }

    @Override
    public Setmeal findSetmealById(int id) {
        return setMealDao.findSetmealById(id);
    }


    // 设置套餐和检查组的多对多关系，操作t_setmeal_checkgroup表
    public void addSetmealAndCheckgroup(Integer setmealId,Integer[] checkgroupIds){
        if (checkgroupIds != null && checkgroupIds.length > 0){
            Map<String, Integer> map = null;
            for (Integer checkgroupId:checkgroupIds){
                map = new HashMap<>();
                map.put("setmealId", setmealId);
                map.put("checkgroupId", checkgroupId);
                setMealDao.addSetmealAndGroups(map);
            }
        }
    }

    // 查询套餐预约占比数据，作为饼状图数据
    @Override
    public List<Map<String, Object>> findSetmealCount() {

        return setMealDao.findSetmealCount();
    }

}
