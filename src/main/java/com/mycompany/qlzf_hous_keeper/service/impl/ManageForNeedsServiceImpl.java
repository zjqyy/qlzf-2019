package com.mycompany.qlzf_hous_keeper.service.impl;

import com.google.gson.Gson;
import com.mycompany.qlzf_hous_keeper.mapper.ManageForNeedsMapper;
import com.mycompany.qlzf_hous_keeper.service.ManageForNeedsService;
import com.mycompany.qlzf_hous_keeper.tools.DateUtil;
import com.mycompany.qlzf_hous_keeper.tools.EsTools;
import com.mycompany.qlzf_hous_keeper.tools.OutData;
import com.mycompany.qlzf_hous_keeper.tools.Tools;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/13 14:38
 */
@Service
public class ManageForNeedsServiceImpl implements ManageForNeedsService {
    @Autowired
    ManageForNeedsMapper manageForNeedsMapper;
    @Autowired
    private TransportClient esClient;
    private static final int PAGE_SIZE = 10;
    private static final String INDEX_NAMESS = "customization_needs";
    private static final String TYPE_NAME = "cd";
    private final Gson gson = new Gson();
    @Override
    public Object get_infoList(String id, String page) {
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
            List<HashMap> coutList = manageForNeedsMapper.get_count(mapsql);
            for (int i = 0; i < coutList.size(); i++) {
                Map map = new HashMap();
                map.put("phone_number", coutList.get(i).get("phone_number").toString());
                map.put("time", coutList.get(i).get("time").toString());
                map.put("shop_id", Integer.parseInt(coutList.get(i).get("shop_id").toString()));
                if (!coutList.get(i).containsKey("real_name")) {
                    map.put("real_name", " ");
                } else {
                    map.put("real_name", coutList.get(i).get("real_name").toString());
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
    public Object update_customizingSearchShop(String info, String shop_id) {
        try {
            HashMap dataMap = gson.fromJson(info, HashMap.class);
            String[] files = {"region", "region_tag", "business_region", "shop_business", "business_type", "shop_tag",
                    "transfer_fee", "areaMin", "areaMax", "monthRentmin", "monthRentMax"};
            for (String each_file : files) {
                if (!dataMap.containsKey(each_file)) {
                    return OutData.softwareFormart("未添加" + each_file);
                } else {
                    if (null == dataMap.get(each_file) || dataMap.get(each_file).equals("")) {
                        return OutData.softwareFormart(each_file + "不能为空");
                    }
                }
            }
            HashMap map_sql = new HashMap();
            map_sql.put("shop_id", shop_id);
            boolean up_es_res = false;
            dataMap.put("id", shop_id);
            up_es_res = EsTools.update_byID(esClient, INDEX_NAMESS, TYPE_NAME, shop_id, dataMap);
            HashMap datatime = new HashMap();
            if (!up_es_res) {
                return OutData.softwareFormart("系统异常");
            }
            datatime.put("date", DateUtil.getNowDateShort());
            return OutData.softwareFormart(datatime);
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("系统异常");
        }
    }

    @Override
    public Object shopDetailForRentSeeking(String shop_id) {
        try{
            if (Tools.check_null(shop_id)) return OutData.softwareFormart("店铺id不能为空");
            Map<String, Object> data_map = EsTools.getdoc_ByID(esClient,INDEX_NAMESS, TYPE_NAME, shop_id).getSourceAsMap();
            HashMap map = new HashMap();
            map.put("shop_id", shop_id);
            List<Map> sql_map =  manageForNeedsMapper.getUsernameAndTel(map);
            if (!sql_map.isEmpty()) {
                data_map.put("name", sql_map.get(0).get("real_name"));
                data_map.put("tel", sql_map.get(0).get("phone_number"));
            }
            return OutData.softwareFormart(data_map);
        }catch (Exception e){
            e.printStackTrace();
            return OutData.softwareFormart("系统异常");
        }
    }
}
