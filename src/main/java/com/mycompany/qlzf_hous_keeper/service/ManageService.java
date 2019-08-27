package com.mycompany.qlzf_hous_keeper.service;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/12 17:45
 */
public interface ManageService {

    Object get_info(String worker_id, String page);

    Object update_info(String info, String shop_id);

    Object count_forToday();

    Object shopDetailForSublet(String shop_id);

    Object inser_user(String shop_id, String worker_id, String user_name, String phone_number, String sex, String from, String type, String info, String info_by, String appointment_time, String isInsert, String title, String user_follweType, String complete_bonus, String complete_time, String follow_time);

    Object get_user_shops_or_wantsShop(String user_id, String type);

    Object get_follow_list(String worker_id, String info, String phone_number, String user_name);

    Object search_listInfo(String worker_id, String user_name, String phone_number, String status, String start_time, String end_time, String publish_time, String page, String type);

    Object get_follow_detail(String follow_id);

    Object failed_reasons(String shop_id, String info);
}
