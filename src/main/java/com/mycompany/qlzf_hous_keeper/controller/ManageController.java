package com.mycompany.qlzf_hous_keeper.controller;

import com.mycompany.qlzf_hous_keeper.service.LoginService;
import com.mycompany.qlzf_hous_keeper.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/12 17:40
 */
@Api(value = "房源管理接口")
@RequestMapping("/ManageController")
@RestController
@Validated
public class ManageController {
    @Autowired
    private ManageService manageService;

    @ApiOperation(value = "登录接口文档", notes = "通过用户名及密码登录账户")
    @GetMapping("/get_info")
    public Object login(@RequestParam(required = false) String worker_id, String page) {
        return (Object) manageService.get_info(worker_id, page);
    }

    @PostMapping("/update_info")
    public Object update_info(@RequestParam(required = false) String info, String shop_id) {
        return (Object) manageService.update_info(info, shop_id);
    }

    @GetMapping("/count_forToday")
    public Object count_forToday() {
        return (Object) manageService.count_forToday();
    }

    @GetMapping("/shopDetailForSublet")
    public Object shopDetailForSublet(@RequestParam(required = false) String shop_id) {
        return (Object) manageService.shopDetailForSublet(shop_id);
    }

    @PostMapping("/inser_user")
    public Object inser_user(@RequestParam(required = false) String shop_id, String worker_id, String user_name, String phone_number, String sex,
                             String from, String type, String info, String info_by, String appointment_time, String isInsert, String title, String user_follweType, String complete_bonus, String complete_time, String follow_time) {
        return (Object) manageService.inser_user(shop_id, worker_id, user_name, phone_number, sex,
                from, type, info, info_by, appointment_time, isInsert, title, user_follweType, complete_bonus, complete_time, follow_time);
    }

    @PostMapping("/get_user_shops_or_wantsShop")
    public Object get_user_shops_or_wantsShop(@RequestParam(required = false) String user_id, String type) {
        return (Object) manageService.get_user_shops_or_wantsShop(user_id, type);
    }

    @GetMapping("/get_follow_list")
    public Object get_user_shops_or_wantsShop(@RequestParam(required = false) String worker_id, String info, String phone_number, String user_name) {
        return (Object) manageService.get_follow_list(worker_id, info, phone_number, user_name);
    }

    @PostMapping("/search_listInfo")
    public Object search_listInfo(@RequestParam(required = false) String worker_id, String user_name, String phone_number, String status, String start_time, String end_time, String publish_time, String page, String type) {
        return (Object) manageService.search_listInfo(worker_id, user_name, phone_number, status, start_time, end_time, publish_time, page, type);
    }

    @GetMapping("/get_follow_detail")
    public Object get_follow_detail(@RequestParam(required = false) String follow_id) {
        return (Object) manageService.get_follow_detail(follow_id);
    }

    @GetMapping("/failed_reasons")
    public Object failed_reasons(@RequestParam(required = false) String shop_id, String info) {
        return (Object) manageService.failed_reasons(shop_id, info);
    }
}
