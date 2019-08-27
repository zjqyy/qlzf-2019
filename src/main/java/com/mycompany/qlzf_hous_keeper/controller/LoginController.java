package com.mycompany.qlzf_hous_keeper.controller;


import com.mycompany.qlzf_hous_keeper.QlzfHousKeeperApplication;
import com.mycompany.qlzf_hous_keeper.entity.GroupsForJudgeEntityIsOrNotNull.GroupA;
import com.mycompany.qlzf_hous_keeper.entity.GroupsForJudgeEntityIsOrNotNull.GroupB;
import com.mycompany.qlzf_hous_keeper.entity.Woker;
import com.mycompany.qlzf_hous_keeper.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Api(value = "登录相关接口")
@RequestMapping("/logingAbout")
@RestController
public class LoginController {
    private static Logger logger = LoggerFactory.getLogger(QlzfHousKeeperApplication.class);
    @Autowired
    private LoginService loginService;



    @ApiOperation(value = "登录接口文档", notes = "通过用户名及密码登录账户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")})
    @PostMapping("/session/login")
    public Object login(@Validated({GroupA.class}) Woker woker) {
        logger.info(" logging..param:"+woker);
        return (Object) loginService.login(woker);
    }



    @ApiOperation(value = "获取个人资料", notes = "通过id获取个人资料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id信息", required = true, dataType = "String", paramType = "query")})
    @GetMapping("/session")
    public Object getMyInfo(@PathVariable @NotNull(message = "登录id不能为空") @RequestParam(required = false) String id) {
        logger.info("getMyInfo param："+id);
        return (Object) loginService.getMyInfo(id);
    }

    @ApiOperation(value = "修改个人信息", notes = "修改个人信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sex", value = "性别", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "城市", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pic_num", value = "图片id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "e_mail", value = "邮箱", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tel", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "id信息", required = true, dataType = "String", paramType = "update")})
    @PutMapping("/session")
    public Object updateMyInfo(@Validated(GroupB.class) Woker woker) {
        logger.info("updateMyInfo..."+woker);
        return (Object) loginService.updateMyInfo(woker);
    }
}