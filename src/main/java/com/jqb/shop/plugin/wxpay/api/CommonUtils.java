package com.jqb.shop.plugin.wxpay.api;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
	
	  /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readFileByLines(String fileName) {
        File file = null;
        try {
            file = new ClassPathResource(fileName).getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = "";
        if (file == null) {
            return result;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            int line = 1;
            String tempString = "";
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	result += new String(tempString.getBytes(),"UTF-8");
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        System.out.println("content: " + result );
        return result;
    }

    /**
     * 将java.util.Date 格式转换为字符串格式'yyyy-MM-dd HH:mm:ss'(24小时制)<br>
     * 如Sat May 11 17:24:21 CST 2002 to '2002-05-11 17:24:21'<br>
     * @param time Date 日期<br>
     * @return String   字符串<br>
     */


    public static String dateToString(Date time){
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String ctime = formatter.format(time);

        return ctime;
    }
    public static String timeStampMillis(){
        String timeStapm = ""+ System.currentTimeMillis();
        timeStapm = timeStapm.substring(0, timeStapm.length()-3);
        return timeStapm;
    }

    public static void main(String[] args) {
        timeStampMillis();
    }
}
