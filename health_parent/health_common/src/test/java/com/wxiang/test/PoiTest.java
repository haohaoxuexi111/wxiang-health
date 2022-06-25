/*
package com.wxiang.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;

public class PoiTest {
    @Test // 使用POI读取Excel文件中的数据
    public void poi() throws Exception{
        // 加载指定文件，创建一个Excel对象(工作簿)
        XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File("C:\\Users\\Xiangtai\\Desktop\\poiTest.xlsx")));
        // 读取Excel文件中第一个Sheet标签页
        XSSFSheet sheet01 = excel.getSheetAt(0);
        // 遍历Sheet标签页中的每一行数据
        for (Row row : sheet01){
            // 遍历当前行每个单元格中的数据
            for (Cell cell : row) {
                // 这里一般需要判断单元格中的数据类型，在根据类型输出。
                System.out.print(cell.getStringCellValue() + "\t");
            }
            System.out.println();
        }
        // 关闭资源
        excel.close();
    }

    @Test // 使用POI读取Excel文件中的数据，获取指定索引范围内的数据
    public void poi1() throws Exception{
        // 加载指定文件，创建一个Excel对象(工作簿)
        XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File("C:\\Users\\Xiangtai\\Desktop\\poiTest.xlsx")));
        // 读取Excel文件中第一个Sheet标签页
        XSSFSheet sheet01 = excel.getSheetAt(0);
        // 获取当前工作表中有数据的最后一个行号，行号从0开始
        int lastRowNum = sheet01.getLastRowNum();
        for (int i=0;i<=lastRowNum;i++){  // 行号要等于
            XSSFRow row = sheet01.getRow(i);  // 根据行号获取每一行
            // 获取当前行有数据的最后一个单元格索引
            short lastCellNum = row.getLastCellNum();
            for (int j=0;j<lastCellNum;j++){ // 列不用等于
                // 根据单元格索引，获取当前行指定索引的单元格
                XSSFCell cell = row.getCell(j);
                System.out.println(cell.getStringCellValue());
            }
        }
        // 关闭资源
        excel.close();
    }

    // 使用POI向Excel文件写入数据，并且通过输出流将创建的Excel文件保存到本地磁盘
    @Test
    public void poi2() throws IOException {
        // 在内存中创建一个Excel文件
        XSSFWorkbook excel = new XSSFWorkbook();
        // 创建一个工作表对象
        XSSFSheet sheet01 = excel.createSheet("学生表");
        // 在工作表中创建行对象
        XSSFRow tr = sheet01.createRow(0);
        // 在行中创建单元格对象
        tr.createCell(0).setCellValue("姓名");
        tr.createCell(1).setCellValue("性别");
        tr.createCell(2).setCellValue("年龄");
        XSSFRow td = sheet01.createRow(1);
        td.createCell(0).setCellValue("小王");
        td.createCell(1).setCellValue("深圳");
        td.createCell(2).setCellValue(18);

        // 创建输出流，通过输出流，将内存中的excel文件写到磁盘
        FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\Xiangtai\\Desktop\\output.xlsx"));
        excel.write(fos);
        fos.flush();  // 写入磁盘
        fos.close();
        excel.close();
    }
}
*/
