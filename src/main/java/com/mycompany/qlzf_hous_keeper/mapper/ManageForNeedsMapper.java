package com.mycompany.qlzf_hous_keeper.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/13 14:42
 */
@Mapper
public interface ManageForNeedsMapper {
    List<HashMap> get_count(Map mapsql);

    List<Map> getUsernameAndTel(HashMap map);
}
