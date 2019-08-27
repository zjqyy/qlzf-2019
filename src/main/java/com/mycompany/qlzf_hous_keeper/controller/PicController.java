package com.mycompany.qlzf_hous_keeper.controller;

import com.mycompany.qlzf_hous_keeper.service.PicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/13 15:54
 */
@Api(value = "图片接口")
@RequestMapping("/picAbout")
@RestController

public class PicController {

    @Autowired
    PicService picService;

    @ApiOperation(value = "上传头像", notes = "上传头像")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "图片名字", required = true, dataType = "String", paramType = "insert")})
    @PostMapping("/pic")
    private Object publish_shop_pic(HttpServletResponse response, HttpServletRequest request) throws IOException {
        return picService.publishPic(response,request);
    }


    @ApiOperation(value = "获取头像", notes = "获取头像")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "zip", value = "是否压缩，yes为压缩，no为不压缩", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "picNum", value = "图片id", required = true, dataType = "String", paramType = "query")})
    @GetMapping("/pic")
    private Object getShopPic(HttpServletResponse response, HttpServletRequest request) throws IOException{
        return picService.getShopPic(response,request);
    }


    @ApiOperation(value = "删除头像", notes = "删除头像")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "picNum", value = "图片id", required = true, dataType = "String", paramType = "query")})
    @DeleteMapping("/pic")
    private Object deletePic(HttpServletResponse response, HttpServletRequest request) throws IOException{
        return picService.deletPic(response,request);
    }
}
