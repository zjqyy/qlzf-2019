/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.qlzf_hous_keeper.tools;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author YJ
 */
public class OutData {

    private static PreparedStatement ps;
    private static Connection con;
    private static ResultSet rs;
    private static final String[] areas = {"中国", "北京", "安徽", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "河南", "黑龙江", "湖北", "湖南", "吉林", "江苏", "江西", "辽宁", "内蒙古", "宁夏", "青海", "山东", "山西", "陕西", "上海", "四川", "天津", "西藏", "新疆", "云南", "浙江", "重庆", "香港", "澳门", "台湾"};
    //地球平均半径  
    private static final double EARTH_RADIUS = 6371.393;

    //把经纬度转为度（°）  
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 计算时间
     *
     * @param day
     * @return
     */
    public static String computeTime(int day) {
        try {
            Date date = new Date();
            Calendar cd = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String today=sdf.format(date);
            cd.setTime(date);
            cd.add(Calendar.DAY_OF_YEAR, day);
            String result = sdf.format(cd.getTime());
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 根据传入的格式和时间 返回格式化的时间数据。
     *
     * @param formart
     * @param time 秒值
     * @return
     */
    public static String formartDate(String formart, long time) {
        if (String.valueOf(time).length() <= 10) {
            time = time * 1000;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formart);
        Date d = new Date(time);
        return sdf.format(d);
    }

    /**
     * 省份转换，数字转换字符串。字符串转换数字入库
     *
     * @param area
     * @return
     */
    public static Object transArea(String area) {
        Object area_id = null;
        int transarea = 0;
        try {
            transarea = Integer.parseInt(area);
//            System.out.println("数字转字符串。");
            for (int i = 0; i < areas.length; i++) {
                if ((transarea - 1) == i) {
                    area_id = areas[i];
                    break;
                }
            }
        } catch (NumberFormatException e) {
//            System.out.println("字符串转数字。");
            for (int i = 0; i < areas.length; i++) {
                if (areas[i].equals(area)) {
                    area_id = String.valueOf(i + 1);
                    break;
                }
            }
        }
        return area_id;
    }

    /**
     * 计算页码
     *
     * @param count
     * @param pageSize
     * @return
     */
    public static int compatePage(long count, int pageSize) {
        int pagecount = -1;
        if (count % pageSize != 0) {
            pagecount = (int) (count / pageSize) + 1;
        } else {
            pagecount = (int) (count / pageSize);
        }
        return pagecount;
    }
    
      public static Map software_Formart_count_pages(List<Map> list, long total,long pages) {
        Map results = new HashMap<>();
        Map ext;
        try {
            //按需格式化
//                count = (long) list.get(list.size() - 1).get("count");
            ext = new HashMap<>();
            ext.put("count", total);
            ext.put("list", list);
            ext.put("pages", pages);
            results.put("state", true);
            results.put("message", "操作成功");
            results.put("ext", ext);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    /**
     * about globalsearch
     *
     *
     */
    /**
     * 特殊方法。按照软件组要求json格式封装结果并返回
     *
     * @param list
     * @param pageSize
     * @return
     */
    public static Map softwareFormart(List<Map<String, String>> list, int pageSize) {
        Map results = new HashMap<>();
        long count;
        int pagecount;
        Map ext;
        try {
            if (!list.isEmpty()) {
                //按需格式化
//                count = (long) list.get(list.size() - 1).get("count");
                ext = new HashMap<>();
                try {
                    count = Integer.parseInt(list.get(list.size() - 1).get("count").toString());
                    pagecount = OutData.compatePage(count, pageSize);
                    ext.put("count", count);
                    ext.put("pagecount", pagecount);
                } catch (Exception e) {

                }
                list.remove(list.size() - 1);
                ext.put("list", list);
                results.put("state", true);
                results.put("message", "操作成功");
                results.put("ext", ext);
            } else {
                ext = new HashMap<>();
                ext.put("count", 0);
                ext.put("pagecount", 0);
                ext.put("list", list);
                results.put("state", true);
                results.put("message", "操作成功");
                results.put("ext", ext);
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    public static Map software_Formart_hashMap(List<HashMap<String, String>> list) {
        Map results = new HashMap<>();
        try {
            results.put("state", true);
            results.put("message", "操作成功");
            results.put("ext", list);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    public static Map software_Formart(List<Map> list) {
        Map results = new HashMap<>();
        try {
            results.put("state", true);
            results.put("message", "操作成功");
            results.put("ext", list);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    public static Map software_Formart(List<Map> list, int pageSize) {
        Map results = new HashMap<>();
        long count;
        int pagecount;
        Map ext;
        try {
            if (!list.isEmpty()) {
                //按需格式化
//                count = (long) list.get(list.size() - 1).get("count");
                ext = new HashMap<>();
                try {
                    count = Integer.parseInt(list.get(list.size() - 1).get("count").toString());
                    pagecount = OutData.compatePage(count, pageSize);
                    ext.put("count", count);
                    ext.put("pagecount", pagecount);
                } catch (Exception e) {

                }
                list.remove(list.size() - 1);
                ext.put("list", list);
                results.put("state", true);
                results.put("message", "操作成功");
                results.put("ext", ext);
            } else {
                ext = new HashMap<>();
                ext.put("count", 0);
                ext.put("pagecount", 0);
                ext.put("list", list);
                results.put("state", true);
                results.put("message", "操作成功");
                results.put("ext", ext);
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    public static Map software_Formart_count(List<Map> list, long total) {
        Map results = new HashMap<>();
        Map ext;
        try {
            //按需格式化
//                count = (long) list.get(list.size() - 1).get("count");
            ext = new HashMap<>();
            ext.put("count", total);
            ext.put("list", list);
            results.put("state", true);
            results.put("message", "操作成功");
            results.put("ext", ext);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    public static Map software_Formart_count(List<Map> list, int total) {
        Map results = new HashMap<>();
        Map ext;
        try {
            //按需格式化
//                count = (long) list.get(list.size() - 1).get("count");
            ext = new HashMap<>();
            ext.put("count", total);
            ext.put("list", list);
            results.put("state", true);
            results.put("message", "操作成功");
            results.put("ext", ext);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    /**
     * 按需格式化
     *
     * @param map
     * @param pageSize
     * @return
     */
    public static Map softwareFormart(Map map) {
        Map results = new HashMap<>();
        Map ext;
        try {
            if (!map.isEmpty()) {
                results.put("state", true);
                results.put("message", "操作成功");
                results.put("ext", map);
            } else {
                ext = new HashMap<>();
//                ext.put("list", map);
                results.put("state", true);
                results.put("message", "操作成功");
                results.put("ext", ext);
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    /**
     * 封装，无list，无count参数。
     *
     * @param map
     * @return
     */
    public static Map software_Formart(Map map) {
        Map ext = new HashMap<>();;
        try {
            if (!map.isEmpty()) {
                ext.put("ext", map);
                ext.put("state", true);
                ext.put("message", "操作成功");
            } else {
                ext.put("ext", new HashMap<>());
                ext.put("state", true);
                ext.put("message", "操作成功");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return ext;
    }

    /**
     * 操作失败调用方法。
     *
     * @param message 错误信息
     * @return Map
     */
    public static Map softwareFormart(String message) {
        Map results = new HashMap<>();
        Map ext;
        try {
            ext = new HashMap<>();
            results.put("state", false);
            results.put("message", message);
            results.put("ext", ext);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    /**
     * 操作成功 无返回参数
     *
     * @return
     */
    public static Map softwareFormart_OK() {
        Map results = new HashMap<>();
        Map ext;
        try {
            ext = new HashMap<>();
            results.put("state", true);
            results.put("message", "操作成功");
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    /**
     *
     * @param type
     * @return
     */
    public static Map selectQuerys(String type) {
        Map params = new HashMap<>();
        switch (type) {
            case "stang_projectinfo":
                params.put("type", type);
                params.put("where", " `project_check` = 1 and id> 121000");
                params.put("database", "suitang2");
                params.put("colNames", "id,projectname,properties,inuest_coont,forid,turnover_time,area_id,intime");
                params.put("title", "项目动态");
                break;
            default:
                break;
        }
        return params;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为KM
     *
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(a / 2), 2)
                        + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)
                )
        );
        s = s * EARTH_RADIUS;
//        s = Math.round(s);
        return s;
    }

    /**
     * 格式化double类型数据，保留小数位后两位
     *
     * @param d
     * @return
     */
    public static double formartDouble(double d) {
        BigDecimal b = new BigDecimal(d);
        double f1 = 0;
        try {
            f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            f1 = -1;
        }
        return f1;
    }

    /**
     * 错误信息封装方法
     *
     * @param message
     * @return
     */
    public static Object FormatBad(String message) {
        Map ext = new HashMap<>(4);
        ext.put("state", false);
        ext.put("message", message);
        return ext;
    }

    public static void main(String[] args) {
//        OutData od=new OutData();
//        System.out.println(od.computeTime(-20));

    }
}
