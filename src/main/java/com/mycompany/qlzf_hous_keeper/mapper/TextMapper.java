package com.mycompany.qlzf_hous_keeper.mapper;

import java.util.HashMap;
import java.util.List;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/8/22 10:18
 */
public interface TextMapper {
    List<HashMap<String, String>> getWorkerTel(HashMap map_sql);

    List<HashMap<String, String>> getShopId();
}
