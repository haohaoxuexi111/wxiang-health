package com.wxiang.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wxiang.constant.MessageConstant;
import com.wxiang.entity.Result;
import com.wxiang.service.MemberService;
import com.wxiang.service.ReportService;
import com.wxiang.service.SetMealService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;
    @Reference
    private SetMealService setMealService;

    // 展示过去12个月每月的会员数量的折线图数据
    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {
        Calendar calendar = Calendar.getInstance();
        Map<String, Object> map = new HashMap<>();
        List<String> months = new ArrayList<>();
        // 计算过去一年的12个月
        calendar.add(Calendar.MONTH, -12);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM");
        Date date = null;
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1);
            date = calendar.getTime();
            months.add(format.format(date));
        }
        map.put("months", months);

        Map<String, Integer> memberCount = memberService.findMemberCountByMonths(months);  //
        System.out.println(memberCount.toString());
        // months.forEach(System.out::print);
        List<Integer> count = new ArrayList<>();  // y 轴对应的数据要放在一个list中

        for (String m : months) {
            memberCount.putIfAbsent(m, 0);
            count.add(memberCount.get(m));

        }
        for (Integer i : count) {
            System.out.print(i + "  ");
        }

        map.put("memberCount", count);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
    }

    // 套餐预约占比饼状图
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() {
        try {
            Map<String, Object> data = new HashMap<>();

            List<Map<String, Object>> setmealCount = setMealService.findSetmealCount();  // 包括套餐名称name和预约数量value的map集合
            List<String> setmealNames = new ArrayList<>();  // 封装图例需要使用的套餐名称
            for(Map<String, Object> map : setmealCount) {
                String name = (String) map.get("name");
                setmealNames.add(name);
            }
            data.put("setmealNames", setmealNames);
            data.put("setmealCount", setmealCount);
            return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }
    }

    @Reference
    private ReportService reportService;

    // 运营数据统计
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        try {
            Map<String, Object> data = reportService.getBusinessReportData();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }

        // 使用模拟数据测试流程是否可以正常展示
        /*map.put("reportDate", "2022.5.19");
        map.put("todayNewMember", 100);
        map.put("totalMember", 200);
        map.put("thisWeekNewMember", 150);
        map.put("thisMonthNewMember", 130);
        map.put("todayOrderNumber", 220);
        map.put("todayVisitsNumber", 140);
        map.put("thisWeekOrderNumber", 120);
        map.put("thisWeekVisitsNumber", 100);
        map.put("thisMonthOrderNumber", 130);
        map.put("thisMonthVisitsNumber", 150);

        List<Map<String, Object>> hotSetmeal = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "嘿嘿嘿");
        map1.put("setmeal_count", 320);
        map1.put("proportion", 0.3);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "哈哈哈");
        map2.put("setmeal_count", 250);
        map2.put("proportion", 0.5);
        hotSetmeal.add(map1);
        hotSetmeal.add(map2);

        map.put("hotSetmeal", hotSetmeal);*/

    }

    // 已excel的形式导出运营数据
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> map = reportService.getBusinessReportData();
            // 取出查询结果，准备将报表数据写入Excel文件中
            String reportDate = (String) map.get("reportDate");
            Integer todayNewMember = (Integer) map.get("todayNewMember");
            Integer totalMember = (Integer) map.get("totalMember");
            Integer thisWeekNewMember = (Integer) map.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) map.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) map.get("todayOrderNumber");
            Integer todayVisitsNumber = (Integer) map.get("todayVisitsNumber");
            Integer thisWeekOrderNumber = (Integer) map.get("thisWeekOrderNumber");
            Integer thisWeekVisitsNumber = (Integer) map.get("thisWeekVisitsNumber");
            Integer thisMonthOrderNumber = (Integer) map.get("thisMonthOrderNumber");
            Integer thisMonthVisitsNumber = (Integer) map.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) map.get("hotSetmeal");

            // request.getSession().getServletContext().getRealPath("template") : 获得template目录的绝对目录
            // File.separator : 根据当前操作系统，返回文件目录分隔符  Windows \\ Linux /
            String filePath = request.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";

            // 基于提供的Excel模板文件在内存中创建一个Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File(filePath)));  // 基于template目录中的模板文件创建一个新的excel文件
            XSSFSheet sheet = excel.getSheetAt(0);  // 读取第一个工作表

            // 行数和列数下标都是从 0 开始的，获得第三行，第六个单元格，并写入数据
            XSSFRow row = sheet.getRow(2);
            XSSFCell cell = row.getCell(5);
            cell.setCellValue(reportDate);

            sheet.getRow(4).getCell(5).setCellValue(todayNewMember);
            sheet.getRow(4).getCell(7).setCellValue(totalMember);
            sheet.getRow(5).getCell(5).setCellValue(thisWeekNewMember);
            sheet.getRow(5).getCell(7).setCellValue(thisMonthNewMember);
            sheet.getRow(7).getCell(5).setCellValue(todayOrderNumber);
            sheet.getRow(7).getCell(7).setCellValue(todayVisitsNumber);
            sheet.getRow(8).getCell(5).setCellValue(thisWeekOrderNumber);
            sheet.getRow(8).getCell(7).setCellValue(thisWeekVisitsNumber);
            sheet.getRow(9).getCell(5).setCellValue(thisMonthOrderNumber);
            sheet.getRow(9).getCell(7).setCellValue(thisMonthVisitsNumber);

            int rowNum = 12;
            for(Map hot : hotSetmeal) {
                String name = (String) hot.get("name");
                Long setmeal_count = (long) hot.get("setmeal_count");  // 这里换成 Integer类型 报 Long 类型转 Integer类型 异常
                BigDecimal proportion = (BigDecimal) hot.get("proportion");
                row = sheet.getRow(rowNum++);
                row.getCell(4).setCellValue(name);
                row.getCell(5).setCellValue(setmeal_count);
                row.getCell(6).setCellValue(proportion.doubleValue());
            }

            // 使用输出流将内存中的excel下载到客户端，基于浏览器下载
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel"); // ms-excel 代表Excel文件类型
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx"); // 指定以附件形式进行下载
            excel.write(outputStream);
            outputStream.flush();
            outputStream.close();
            excel.close();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

}
