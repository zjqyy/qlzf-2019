/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.qlzf_hous_keeper.tools;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author zjq
 */
public class DateUtil {

    public static String pattern = "yyyy-MM-dd";
    public static SimpleDateFormat formatter = new SimpleDateFormat(pattern);
    public static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 返回时间类型 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static Date getNowDate() {
        String dateString = formatter2.format(new Date());
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     *
     * @return 返回短时间格式 yyyy-MM-dd
     */
    public static String getNowDateShort() {
        String dateString = formatter.format(new Date());
       
        return dateString;
    }
}
