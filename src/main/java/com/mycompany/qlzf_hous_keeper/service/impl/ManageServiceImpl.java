package com.mycompany.qlzf_hous_keeper.service.impl;

import com.mycompany.qlzf_hous_keeper.mapper.ManageMapper;
import com.mycompany.qlzf_hous_keeper.service.ManageService;
import com.mycompany.qlzf_hous_keeper.tools.EsTools;
import com.mycompany.qlzf_hous_keeper.tools.OutData;
import com.mycompany.qlzf_hous_keeper.tools.Tools;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/12 17:45
 */
@Service
public class ManageServiceImpl implements ManageService {
    private final Gson gson = new Gson();
    private final Map repairmap = new HashMap();
    private static final int PAGE_SIZE = 10;
    private static final String INDEX_NAME = "fql_rent";
    private static final String INDEX_NAMESS = "customization_needs";
    private static final String TYPE_NAME = "cd";
    @Autowired
    ManageMapper manageMapper;
    @Autowired
    private TransportClient esClient;
    @Override
    public Object get_info(String id, String page) {
        List<Map> dataList = new ArrayList<>();
        try {
            if (Tools.check_null(id)) {
                return OutData.softwareFormart("工作人员id不能为空");
            }
            if (Tools.check_null(page)) {
                return OutData.softwareFormart("页数不能为空");
            }
            int start = (Integer.valueOf(page.toString()) - 1) * PAGE_SIZE;
            Map mapsql = new HashMap();
            mapsql.put("id", id);
            List<HashMap> coutList = manageMapper.get_count(mapsql);
            //  int user_id = Integer.parseInt(coutList.get(0).get("id").toString());
            for (int i = 0; i < coutList.size(); i++) {
                Map map = new HashMap();
                map.put("phone_number", coutList.get(i).get("phone_number").toString());
                map.put("time", coutList.get(i).get("time").toString());
                map.put("shop_id", Integer.parseInt(coutList.get(i).get("shop_id").toString()));
                if (Tools.check_null(coutList.get(i).get("name").toString())) {
                    map.put("name", "");
                } else {
                    map.put("name", coutList.get(i).get("name").toString());
                }
                dataList.add(map);
            }
            int total = coutList.size();
            long total_page = total / PAGE_SIZE;
            if (total % PAGE_SIZE > 0) {
                total_page += 1;
            }
            return OutData.software_Formart_count_pages(dataList, total, total_page);
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统异常");
        }
    }

    @Override
    public Object update_info(String info, String shop_id) {
        boolean add_mysql = false;
        boolean add_es_res = false;
        boolean add_to_hbase = false;
        try {
            if (Tools.check_null(info)) {
                return OutData.softwareFormart("数据不能为空");
            }
            if (Tools.check_null(shop_id)) {
                return OutData.softwareFormart("shop_id不能为空");
            }
            HashMap dataMap = gson.fromJson(info, HashMap.class);
            //去除空值
            dataMap = remove_null(dataMap);
            //检验数据完整性
            String status = check_info(dataMap);
            if (!status.equals("ok")) {
                return OutData.softwareFormart(status);
            }
            //预处理数据
            dataMap = deal_info(dataMap);
            //初始化sql需要数据
            HashMap map_sql = new HashMap();
            map_sql.put("title", dataMap.get("title"));
            map_sql.put("shop_id", shop_id);
            List<Map> response_user_id = manageMapper.getUser_id_byId(map_sql);
            if (response_user_id.isEmpty()) {
                return OutData.softwareFormart("未查询到用户");
            }
            String user_id = response_user_id.get(0).get("user_id").toString();
            String old_status = response_user_id.get(0).get("status").toString();
            repairmap.put("status", old_status);
            repairmap.put("shop_id", shop_id);
            map_sql.put("status", 2);
            manageMapper.update_title(map_sql);
            HashMap map_es = get_es_map(dataMap, Integer.parseInt(user_id), shop_id);
            map_es.put("status", "2");
            String id = map_es.remove("_id").toString();
            System.out.println(map_es.get("geo"));
            add_es_res = EsTools.update_byID(esClient, INDEX_NAME, TYPE_NAME, id, map_es);
            if (!add_es_res) return OutData.softwareFormart("es存储失败");
            // add_to_hbase = add_to_hbase(dataMap, shop_id);
            return OutData.softwareFormart_OK();
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统异常");
        }
    }

    @Override
    public Object count_forToday() {
        try {
            LocalDate day = LocalDate.now();
            HashMap map = new HashMap();
            map.put("day", day);
            int subletCount = 0;
            int needsCount = 0;
            List<Map> shop_count = manageMapper.getInfo_todayUser_shop(map);
            Map mapRes = new HashMap();
            List<Map> wantCount = manageMapper.getInfo_todayUser_wantshop(map);
            if (!shop_count.isEmpty()) subletCount = shop_count.size();
            if (!wantCount.isEmpty()) needsCount = wantCount.size();
            mapRes.put("subletCount", subletCount);
            mapRes.put("needsCount", needsCount);
            return OutData.softwareFormart(mapRes);
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统出错");
        }
    }

    @Override
    public Object shopDetailForSublet(String id) {
        try {
            if (null == id || id.equals("")) {
                return OutData.softwareFormart("shop_id不能为空");
            }
            Map<String, Object> data_map = EsTools.getdoc_ByID(esClient,INDEX_NAME, TYPE_NAME, id).getSourceAsMap();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
            data_map.put("time", sdf.format(new Date(Long.valueOf(data_map.get("time").toString()))));
            return OutData.software_Formart(data_map);
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统出错");
        }
    }

    @Override
    /**
     *
     * @param shop_id           店铺id;
     * @param worker_id        工作人员id
     * @param user_name        用户称呼
     * @param phone_number     电话号码
     * @param sex              性别
     * @param from             客户来源
     * @param type             客户需求
     * @param info             客户谈话信息
     * @param info_by          谈话信息通过方式
     * @param appointment_time 下次预约时间
     * @param isInsert          首录还是跟进
     * @param title             跟进店铺
     * @param user_followType    跟进的状态
     * @param complete_bonus     奖金
     * @param complete_time        完成时间
     * @param follow_time        跟进时间
     * @return
     */

    public Object inser_user(String shop_id,String worker_id, String user_name, String phone_number,
                             String sex, String from, String type, String info,
                             String info_by, String appointment_time, String isInsert,
                             String title,String user_followType,String complete_bonus,
                             String complete_time ,String follow_time) {
        try {
            if (Tools.check_null(isInsert)) return OutData.softwareFormart("跟进还是首录");
            if (Tools.check_null(info_by)) return OutData.softwareFormart("跟进方式不能为空");
            LocalDate day = LocalDate.now();
            HashMap map = new HashMap();
            map.put("worker_id", worker_id);
            map.put("follow_time", follow_time);
            map.put("user_name", user_name);
            map.put("phone_number", phone_number);
            map.put("sex", sex);
            map.put("from", from);
            map.put("type", type);
            map.put("info", info);
            map.put("info_by", info_by);
            map.put("appointment_time", appointment_time);
            map.put("shop_id", shop_id);
            if (isInsert.equals("1")) {
                if (Tools.check_null(follow_time)) return OutData.softwareFormart("首录时间不能为空");
                if (Tools.check_null(worker_id)) return OutData.softwareFormart("还未登录或登录异常");
                if (Tools.check_null(user_name)) return OutData.softwareFormart("请填写客户的姓名或称呼");
                if (Tools.check_null(phone_number)) return OutData.softwareFormart("请填写客户的电话号码并确定电话号码正确！");
                if (Tools.check_null(sex)) return OutData.softwareFormart("请填写客户的性别");
                if (Tools.check_null(from)) return OutData.softwareFormart("请填写客户的来源");
                if (Tools.check_null(type)) return OutData.softwareFormart("请填写客户的需求，转租或订制");
                if (Tools.check_null(info)) return OutData.softwareFormart("跟进信息不能为空");
                if (Tools.check_null(appointment_time)) return OutData.softwareFormart("预约时间不能为空");
                List<Map> data = manageMapper.checkUser_if_exist(map);
                if (data.isEmpty())  manageMapper.inser_user(map);//插入到user_info表，特指还未使用网站的客户；
                List<Map> data_follow = manageMapper.checkFollow(map);
                if (data_follow.isEmpty()) {
                    manageMapper.insert_woker_user_follow(map);
                }else  return OutData.softwareFormart("该用户已经首录过了，请点击跟进该用户");
            }
            if (isInsert.equals("2")) {
                if (Tools.check_null(worker_id)) return OutData.softwareFormart("还未登录或登录异常");
                if (Tools.check_null(info)) return OutData.softwareFormart("跟进信息不能为空");
                if (Tools.check_null(type)) return OutData.softwareFormart("请填写客户的需求，转租或订制");
                if (Tools.check_null(phone_number)) return OutData.softwareFormart("请填写客户的电话号码并确定电话号码正确！");
                if (Tools.check_null(shop_id)) return OutData.softwareFormart("shop_id不能为空");
                if (Tools.check_null(title)) return OutData.softwareFormart("跟进店铺不能为空");
                if (Tools.check_null(user_followType)) return OutData.softwareFormart("跟进的状态不能为空");
                if (Tools.check_null(follow_time)) return OutData.softwareFormart("跟进时间不能为空");
                if (Tools.check_null(appointment_time)) return OutData.softwareFormart("预约时间不能为空");
                List<Map> data_follow = manageMapper.checkFollow(map);
                if (data_follow.isEmpty()) return OutData.softwareFormart("该用户还未录入，请先录入");
                //isInsert=1时，user_follweType是写死在sql里的为1；
                map.put("title", title);
                map.put("user_followType", user_followType);
                map.put("follow_time", follow_time);
                //user_follweType=3代表成交
                if (user_followType.equals("3")){
                    //成交时，不传预约时间；
                    map.remove("appointment_time");
                    map.put("complete_bonus", complete_bonus);
                    map.put("complete_time", complete_time);
                    //成交必须填成交奖励金；
                    if (Tools.check_null(complete_bonus)) return OutData.softwareFormart("成交奖励金额不能为空");
                    //点击成交后，成交时间自动填入；跟进时间和获取时间都是由获取跟进详情时就自动填入；
                    if (Tools.check_null(complete_time)) return OutData.softwareFormart("成交时间不能为空");
                    //转租；修改mysql的shop表的status为3，已租
                    if (type.equals("1")){
                        HashMap dataMap = new HashMap();
                        dataMap.put("status","3");
                        manageMapper.updata_shop_status(map);
                        EsTools.update_byID(esClient, INDEX_NAME, TYPE_NAME, shop_id, dataMap);
                    }
                    //求租；修改mysql的user_wantshop的status为2，代表找到了；
                    if (type.equals("2")){
                        HashMap dataMaps = new HashMap();
                        dataMaps.put("status","2");
                        manageMapper.updata_wantShop_status(map);
                        EsTools.update_byID(esClient, INDEX_NAMESS, TYPE_NAME, shop_id, dataMaps);
                    }
                }
                manageMapper.upadate_woker_user_follow(map);
            }
            return OutData.softwareFormart_OK();
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统出错");
        }
    }

    @Override
    public Object get_user_shops_or_wantsShop(String user_id, String type) {
        try {
            if (Tools.check_null(user_id)) return OutData.softwareFormart("客户id不能为空");
            if (Tools.check_null(type)) return OutData.softwareFormart("客户类型不能为空");
            HashMap map = new HashMap();
            map.put("user_id", user_id);
            List<Map> response;
            List<Map> MapList = new ArrayList<>();
            Map<String, String> mapTitle = new HashMap<>();
            //typey=1为转租
            if (type.equals("1")) {
                response = manageMapper.get_user_shops(map);
                for (Map eachMap : response) {
                    mapTitle.put("id",String.valueOf(eachMap.get("id")));
                    mapTitle.put("title",String.valueOf(eachMap.get("title")));
                    MapList.add(mapTitle);
                }
                //typey=2为求租
            } else if (type.equals("2")) {
                response = manageMapper.get_user_wantsShops(map);
                for (Map eachMap : response) {
                    String id = String.valueOf(eachMap.get("id"));
                    GetResponse get = EsTools.getdoc_ByID(esClient,INDEX_NAMESS, TYPE_NAME, id);
                    Map<String, Object> temMap;
                    temMap = get.getSourceAsMap();
                    if (temMap == null) {
                        temMap = new HashMap<>();
                        temMap.put(id, "");
                        continue;
                    }
                    String title = "";
                    //shop_tag是后期加入的，所以前期的数据里有些数据没有shop_tag
                    if (temMap.containsKey("region_tag") && temMap.containsKey("shop_tag")) {
                        title = "求租" + temMap.get("region_tag") + temMap.get("shop_tag");
                    }
                    mapTitle.put("id",String.valueOf(temMap.get("id")));
                    mapTitle.put("title",title);
                    MapList.add(mapTitle);
                }
            }
            return OutData.software_Formart(MapList);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return OutData.softwareFormart("系统异常");
        }
    }

    @Override
    public Object get_follow_list(String worker_id, String info, String phone_number, String user_name) {
        try {
            if (Tools.check_null(worker_id)) return OutData.softwareFormart("工作人员id不能为空");
            HashMap map = new HashMap();
            map.put("worker_id", worker_id);
            map.put("info", "%" + info + "%");
            map.put("phone_number", phone_number);
            map.put("user_name", "%" + user_name + "%");
            List<Map> response = manageMapper.search_FollowInfolist(map);
            for (Map eachMap : response) {
                transform(eachMap);
            }
            int total = response.size();
            long total_page = total / PAGE_SIZE;
            if (total % PAGE_SIZE > 0) {
                total_page += 1;
            }
            return OutData.software_Formart_count_pages(response, total, total_page);
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统异常");
        }
    }

    @Override
    /**
     * @param worker_id    工作人员id
     * @param user_name    客户姓名
     * @param phone_number 客户电话号码
     * @param status       店铺状态，1.已通过，2.未通过，3.待审核
     * @param start_time   开始
     * @param end_time     结束
     * @param publish_time 发布时间
     * @param page         页数
     * @param type         1.转租 2.求租
     * @return
     */
    public Object search_listInfo(String worker_id, String user_name, String phone_number, String status, String start_time, String end_time, String publish_time, String page, String type) {
        try {
            if (Tools.check_null(worker_id)) return OutData.softwareFormart("工作人员id不能为空");
            if (Tools.check_null(status)) return OutData.softwareFormart("店铺状态不能为空，已通过，未通过，待审核");
            if (Tools.check_null(page)) return OutData.softwareFormart("页数不能为空");
            if ((!Tools.check_null(start_time) || !Tools.check_null(end_time)) && !Tools.check_null(publish_time))
                return OutData.softwareFormart("开始时间或者截止时间，不能和具体发布时间同时查询");
            HashMap map = new HashMap();
            map.put("worker_id", worker_id);
            map.put("user_name", "%" + user_name + "%");
            map.put("phone_number", phone_number);
            map.put("start_time", start_time);
            map.put("end_time", end_time);
            map.put("publish_time", publish_time);
            List<Map> response = null;
            if (type.equals("1")) {
                map.put("status", status);
                response = manageMapper.search_listInfo(map);//搜索转租信息
            } else {
                map.put("status", 0);
                response = manageMapper.search_listInfodDZ(map); //搜索订制店铺信息
            }
            int total = response.size();
            long total_page = total / PAGE_SIZE;
            if (total % PAGE_SIZE > 0) {
                total_page += 1;
            }
            return OutData.software_Formart_count_pages(response, total, total_page);
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统出错");
        }
    }

    @Override
    /**
     * 通过跟进表的id获取跟进的详情信息
     *
     * @param follow_id
     * @return
     */
    public Object get_follow_detail(String follow_id) {
        try {
            if (Tools.check_null(follow_id)) return OutData.softwareFormart("跟进信息的id不能为空");
            HashMap map = new HashMap();
            map.put("follow_id", follow_id);
            List<Map> response = manageMapper.get_follow_detail(map);
            for (Map eachMap : response) {
                //user_from 1：自己登录的客户；2：主动打咨询电话客户；3；工作人员电话新增；4，朋友介绍；5.网络，网上推广；6.房发现；7其他
                transform(eachMap);
            }
            return OutData.software_Formart(response);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return OutData.softwareFormart("系统异常");
        }
    }

    @Override
    public Object failed_reasons(String shop_id, String info) {
        try {

            if (Tools.check_null(shop_id)) return OutData.softwareFormart("店铺id不能为空");
            if (Tools.check_null(info)) return OutData.softwareFormart("审核未通过原因不能为空");
            if (!info.matches("\\D{10,100}")) return OutData.softwareFormart("审核未通过原因字数应大于等于10小于等于100");
            LocalDate time = LocalDate.now();
            HashMap map = new HashMap();
            map.put("time", time.toString());
            map.put("shop_id", shop_id);
            map.put("info", info);
            List<Map> response = manageMapper.check_if_shenghe(map);
            if (response.isEmpty()) return OutData.softwareFormart("该店铺信息已经审核过了！");
            manageMapper.innert_failed_reasons(map);
            return OutData.softwareFormart_OK();
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统出错");
        }
    }

    /**
     * 将下列参数由数字转换成对应的含义
     * user_type 1:求租、3:转租 ；
     * woker_type：1：租赁一部 2：租赁二部
     * follow_status：1：今日已跟；2：今日未更；
     * user_follweType：1.新增，2.实勘 3.成交 4.下架
     * user_from：1：自己登录的客户；2：主动打咨询电话客户；3；工作人员电话新增；4，朋友介绍；5.网络，网上推广；6.房发现；7其他
     *
     * @param eachMap
     */
    private void transform(Map eachMap) {
        try {
            if (String.valueOf(eachMap.get("user_type")).equals("1")) {
                eachMap.replace("user_type", "求租");
            } else if (String.valueOf(eachMap.get("user_type")).equals("3")) {
                eachMap.replace("user_type", "转租");
            }
            if (String.valueOf(eachMap.get("woker_type")).equals("1")) {
                eachMap.replace("woker_type", "租赁1部");
            } else if (String.valueOf(eachMap.get("woker_type")).equals("2")) {
                eachMap.replace("woker_type", "租赁2部");
            }
            if (String.valueOf(eachMap.get("follow_status")).equals("1")) {
                eachMap.replace("follow_status", "今日已更");
            } else if (String.valueOf(eachMap.get("follow_status")).equals("2")) {
                eachMap.replace("follow_status", "今日未更");
            }
            //1.新增；2；到访；3。成交；4。退单
            if (String.valueOf(eachMap.get("user_follweType")).equals("1")) {
                eachMap.replace("user_follweType", "新增");
            } else if (String.valueOf(eachMap.get("user_follweType")).equals("2")) {
                eachMap.replace("user_follweType", "到访");
            } else if (String.valueOf(eachMap.get("user_follweType")).equals("3")) {
                eachMap.replace("user_follweType", "成交");
            } else if (String.valueOf(eachMap.get("user_follweType")).equals("4")) {
                eachMap.replace("user_follweType", "退单");
            }
            //user_from 1：自己登录的客户；2：主动打咨询电话客户；3；工作人员电话新增；4，朋友介绍；5.网络，网上推广；6.房发现；7其他
            HashMap map = new HashMap();
            map.put("1", "自己登录的客户");
            map.put("2", "主动打咨询电话客户");
            map.put("3", "工作人员电话新增");
            map.put("4", "朋友介绍");
            map.put("5", "网络");
            map.put("6", "房发现");
            map.put("7", "其他");
            eachMap.replace("user_from", map.get(String.valueOf(eachMap.get("user_from"))));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 回调数据 只针对新增数据报错
     */
    private void rollback(Map map) {
        try {
            manageMapper.repair(map);
            EsTools.del_byID(esClient, INDEX_NAME, TYPE_NAME, map.get("shop_id").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param dataMap 总的字段转的map
     * @param user_id 前端传入的token
     * @param
     * @return
     */
    private HashMap get_es_map(HashMap dataMap, int user_id, String shop_id) {
        HashMap map_es = new HashMap();
        String[] es_no_deal_files = {"shop_type", "shop_status", "acreage", "floor_type", "start_floor", "end_floor",
                "total_floor", "wide", "high", "deep", "is_street", "area", "business_area", "address",
                "area_tag", "rent_type", "status", "month_rent", "trans_fee", "first_pic", "title", "licence", "video_id",
                "payment", "start_month", "free_month", "property", "electricity", "water", "info",
                "pic_id", "uname", "tell", "shop_business_tag"};
        for (String each_file : es_no_deal_files) {
            if (dataMap.containsKey(each_file)) {
                map_es.put(each_file, dataMap.get(each_file));
            }
        }
        if (null == dataMap.get("shop_business")) {
            map_es.put("shop_business", 0);
        } else {
            String[] shop_business = dataMap.get("shop_business").toString().split(",");
            List<String> list_shop = Arrays.asList(shop_business);
            map_es.put("shop_business", list_shop);
        }

        map_es.replace("trans_fee", "面议", 1);//转让费为面议的设置成“”；出租的时候，没有存这一字段
        map_es.put("floor_count", (int) (dataMap.get("end_floor")) - (int) (dataMap.get("start_floor")) + 1);
        String[] shop_matchStrings = dataMap.get("shop_match").toString().split(",");
        List<String> list = Arrays.asList(shop_matchStrings);
        map_es.put("shop_match", list);
        System.err.println(dataMap);
        String[] geos = dataMap.get("geo").toString().split(",");
        String geo = geos[1] + "," + geos[0];
        map_es.put("geo", geo);
        map_es.put("area_rent", (int) (dataMap.get("month_rent")) / (int) (dataMap.get("acreage")));
        map_es.put("user_id", user_id);
        map_es.put("id", shop_id);
        map_es.put("_id", shop_id);
        return map_es;
    }


    /**
     * 预处理数据
     *
     * @param dataMap
     * @return
     */
    private HashMap deal_info(HashMap dataMap) {
        dataMap = doubleToIntFilter(dataMap);
        BigDecimal bi = new BigDecimal(dataMap.get("tell").toString());
        dataMap.put("tell", bi.toPlainString());
        return dataMap;
    }

    private HashMap doubleToIntFilter(HashMap dataMap) {
        try {
            String[] int_file = {"rent_type", "status", "shop_type", "shop_status", "acreage", "floor_type", "start_floor",
                    "end_floor", "total_floor", "is_street", "area", "business_area", "month_rent", "trans_fee", "start_month",
                    "free_month"};
            for (String each_file : int_file) {
                if (dataMap.containsKey(each_file)) {
                    if (dataMap.get(each_file).equals("面议")) {
                        continue;
                    }
                    String[] value = dataMap.get(each_file).toString().split("\\.");
                    int a = Integer.parseInt(value[0]);
                    dataMap.put(each_file, a);
                }
            }
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param dataMap
     * @return
     */
    private String check_info(Map dataMap) {
        String[] files = {"shop_type", "shop_status", "acreage", "floor_type", "start_floor",
                "end_floor", "total_floor", "wide", "high", "deep", "is_street", "shop_match", "area", "area_tag",
                "business_area", "address", "geo", "payment", "title", "info", "pic_id", "first_pic", "uname", "tell"};
        for (String each_file : files) {
            if (null == dataMap.get(each_file) || dataMap.get(each_file).equals("")) {
                return each_file + "不能为空";
            }
        }
        String rent_type = dataMap.get("rent_type").toString();
        return "ok";
    }


    private HashMap remove_null(HashMap dataMap) {
        HashMap reMap = new HashMap();
        Iterator iter = dataMap.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry entry = (Map.Entry) iter.next();
            if (!entry.getValue().equals("")) {
                reMap.put(entry.getKey(), entry.getValue());
            }
        }
        return reMap;
    }
}
