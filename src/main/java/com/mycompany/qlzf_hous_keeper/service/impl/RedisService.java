/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.qlzf_hous_keeper.service.impl;

import com.google.gson.Gson;
import com.mycompany.qlzf_hous_keeper.config.redis.RedisUtil;
import com.mycompany.qlzf_hous_keeper.tools.Constant;
import com.mycompany.qlzf_hous_keeper.tools.OutData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * @author jieyan
 */
@Service
@Component("Redis_service")
public class RedisService {

    @Autowired
    private RedisUtil redisUtil;
    private final Gson gson = new Gson();

    public Object get(String token) {
        try {
            return redisUtil.get(token);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean check_geo(Object user_id, String geo, HttpServletResponse response) throws IOException {
        try {
            return redisUtil.hHasKey("lsjl:" + user_id, geo);
        } catch (Exception e) {
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.println(gson.toJson(OutData.softwareFormart("系统异常"), Map.class));
            return false;
        }
    }

    public boolean check_count(Object user_id, HttpServletResponse response) throws IOException {
        try {
            PrintWriter out = response.getWriter();
            Object count = redisUtil.get("sycs:" + user_id);
            if (null == count) {
                out.println(gson.toJson(OutData.softwareFormart("系统异常"), Map.class));
                return false;
            } else {
                if ((int) count < 1) {
                    out.println(gson.toJson(OutData.softwareFormart("今日查询额度已用完，请完善个人资料或分享网页增加额外的查询次数"), Map.class));
                    return false;
                }
            }
//            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.println(gson.toJson(OutData.softwareFormart("系统异常"), Map.class));
            out.close();
            return false;
        }
    }

    public boolean decr_count(Object user_id, String geo, HttpServletResponse response) throws IOException {
        try {
            PrintWriter out = response.getWriter();
            if (!redisUtil.decr_boo("sycs:" + user_id, 1)) {
                out.println(gson.toJson(OutData.softwareFormart("系统异常"), Map.class));
                return false;
            }
            Random random = new Random();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.HOUR_OF_DAY, Constant.redis.SYSTEM_RESET_TIME_H);
            cal.set(Calendar.MINUTE, random.nextInt(Constant.redis.SYSTEM_RESET_TIME_M));
            cal.set(Calendar.SECOND, random.nextInt(60));
            if (!redisUtil.hset("lsjl:" + user_id, geo, cal.getTime())) {
                out.println(gson.toJson(OutData.softwareFormart("系统异常"), Map.class));
            }
//            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.println(gson.toJson(OutData.softwareFormart("系统异常"), Map.class));
            out.close();
            return false;
        }
    }

}
