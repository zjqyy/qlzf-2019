package com.mycompany.qlzf_hous_keeper.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/15 10:07
 */
public interface PicService {
    /**
     * 上传头像图片
     * @param response
     * @param request
     * @return
     * @throws IOException
     */
    Object publishPic(HttpServletResponse response, HttpServletRequest request) throws IOException;

    /**
     * 获取头像图片
     * @param response
     * @param request
     * @return
     * @throws IOException
     */
    Object getShopPic(HttpServletResponse response, HttpServletRequest request) throws IOException;

    /**
     * 删除图片接口
     * @param response
     * @param request
     * @return
     */
    Object deletPic(HttpServletResponse response, HttpServletRequest request);
}
