package com.mycompany.qlzf_hous_keeper.service;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/13 14:38
 */
public interface ManageForNeedsService {
    Object get_infoList(String worker_id, String page);

    Object update_customizingSearchShop(String info, String shop_id);

    Object shopDetailForRentSeeking(String shop_id);
}
