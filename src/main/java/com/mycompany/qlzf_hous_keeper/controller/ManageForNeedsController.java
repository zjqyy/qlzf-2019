package com.mycompany.qlzf_hous_keeper.controller;

import com.mycompany.qlzf_hous_keeper.service.ManageForNeedsService;
import com.mycompany.qlzf_hous_keeper.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/13 14:36
 */
@RequestMapping("/ManageController")
@RestController
@Validated
public class ManageForNeedsController {
    @Autowired
    private ManageForNeedsService manageForNeedsService;
    @GetMapping("/get_infoList")
    public Object get_infoList(@RequestParam(required = false) String worker_id, String page) {
        return (Object) manageForNeedsService.get_infoList(worker_id, page);
    }
    @PostMapping("/update_customizingSearchShop")
    public Object update_customizingSearchShop(@RequestParam(required = false) String info, String shop_id) {
        return (Object) manageForNeedsService.update_customizingSearchShop(info, shop_id);
    }

    @GetMapping("/shopDetailForRentSeeking")
    public Object shopDetailForRentSeeking(@RequestParam(required = false) String shop_id) {
        return (Object) manageForNeedsService.shopDetailForRentSeeking(shop_id);
    }
}
