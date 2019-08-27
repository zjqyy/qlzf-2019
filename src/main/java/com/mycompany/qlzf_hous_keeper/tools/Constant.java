/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.qlzf_hous_keeper.tools;

import java.util.Random;

/**
 *
 * @author Administrator
 */
public class Constant {

    public static class ES {

        public static final String ES_CLUSTER_NAME = "FBD_ES";
        public static final String ES_HOST = "192.168.0.253";
        public static final int ES_PORT = 9301;
    }

    public static class redis {

        public static final int INIT_NUMBER = 10; //普通用户初始查询次数
        public static final int CONPANY_INIT_NUMBER = 100; //企业管理员初始化查询次数
        public static final int CONPANY_PEOPLE_INIT_NUMBER = 100; //企业员工初始化查询次数
        public static final int SYSTEM_RESET_TIME_H = 3; //系统维护时间 时
        public static final int SYSTEM_RESET_TIME_M = 20;  //系统维护截止时间  分      
    }

    public static class baidu {

        //    百度经纬度的ak
        public static final String[] AK = {"vQl4Z7COjewFPFZH2hkEaxI0YuQ7Ylje", "MCljGKnTxOXVdEGEAspP1A28cYE2ov1G",
            "Gc0LBnfNDkT05KYP1LhR0SGLnx2rK9ru", "bxH2dUPxe6n94WUtzF9dyRKyI7CfRjQb",
            "2OCfnvPmiQvLYhZd9FYqYMV39F4sxj2U", "FNNoUAXsZ56ArIytzX2qQIpNacWUPVBZ",
            "nIQHEy1BTZhV721OYNWAFIDMkpKvOHfx", "O0XWFQwyIpEQGO69Gq6W3mHAEwddyxLq"};

        public static String getAK() {
            try {
                Random r = new Random();
                return AK[r.nextInt(AK.length)];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
