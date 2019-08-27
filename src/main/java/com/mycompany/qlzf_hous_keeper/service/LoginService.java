package com.mycompany.qlzf_hous_keeper.service;
import com.mycompany.qlzf_hous_keeper.entity.Woker;

public interface LoginService {

    /**
     * 登录
     * @param woker
     * @return
     */
    Object login(Woker woker) ;

    /**
     * 获取个人信息
     * @param id
     * @return
     */
    Object getMyInfo(String id);

    /**
     * 修改个人信息
     * @param woker
     * @return
     */
    Object updateMyInfo(Woker woker);
}
