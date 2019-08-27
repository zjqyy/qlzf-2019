package com.mycompany.qlzf_hous_keeper.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/12 17:55
 */
@Mapper
public interface ManageMapper {
    List<HashMap> get_count(Map mapsql);

    List<Map> getUser_id_byId(HashMap map_sql);

    void update_title(HashMap map_sql);

    void repair(Map map);

    List<Map> getInfo_todayUser_shop(HashMap map);

    List<Map> getInfo_todayUser_wantshop(HashMap map);

    List<Map> checkUser_if_exist(HashMap map);

    void inser_user(HashMap map);

    List<Map> checkFollow(HashMap map);

    void insert_woker_user_follow(HashMap map);

    void updata_shop_status(HashMap map);

    void updata_wantShop_status(HashMap map);

    void upadate_woker_user_follow(HashMap map);

    List<Map> get_user_shops(HashMap map);

    List<Map> get_user_wantsShops(HashMap map);

    List<Map> search_FollowInfolist(HashMap map);

    List<Map> search_listInfo(HashMap map);

    List<Map> search_listInfodDZ(HashMap map);

    List<Map> get_follow_detail(HashMap map);

    List<Map> check_if_shenghe(HashMap map);

    void innert_failed_reasons(HashMap map);
}
