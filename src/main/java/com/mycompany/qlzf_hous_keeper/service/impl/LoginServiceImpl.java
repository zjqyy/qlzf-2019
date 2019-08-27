package com.mycompany.qlzf_hous_keeper.service.impl;

import com.mycompany.qlzf_hous_keeper.config.redis.RedisUtil;
import com.mycompany.qlzf_hous_keeper.entity.Woker;
import com.mycompany.qlzf_hous_keeper.service.LoginService;
import com.mycompany.qlzf_hous_keeper.mapper.LoginMapper;
import com.mycompany.qlzf_hous_keeper.tools.OutData;

import com.mycompany.qlzf_hous_keeper.tools.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {
    Logger loggerA = LoggerFactory.getLogger(getClass());
    @Autowired
    LoginMapper loginMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public Object login(Woker woker) {
        try{
            List<HashMap> data = loginMapper.checkUser(woker);
            if (data.isEmpty()) {
                return OutData.softwareFormart("用户名不存在或者密码错误");
            }
            String id = String.valueOf(data.get(0).get("id"));
            Random ra = new Random();
            String content_id = String.valueOf(System.currentTimeMillis() + ra.nextInt(1000));
            //token设置32位加密机上末尾的id
            String ID = Tools.MD5(content_id, id);
            boolean boo = false;
            boo = redisUtil.set(ID, id, 7776000);   //手机端登录  保持登录90天
            if (!boo) {
                return OutData.softwareFormart("登录失败");
            }
            HashMap maps = new HashMap();
            maps.put("id", ID);
            maps.put("role", data.get(0).get("role"));
            maps.put("pic_num", data.get(0).get("pic_num"));
            return OutData.softwareFormart(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return OutData.softwareFormart("网络异常");
        }
    }

    @Override
    public Object getMyInfo(String id) {
        try {
            Map map = new HashMap();
            map.put("id", id);
            List<HashMap> data = loginMapper.get_myInfo(map);
            if (data.isEmpty()) {
                return OutData.softwareFormart("该用户不存在，请联系管理员");
            }
            return OutData.softwareFormart(data.get(0));
        } catch (Exception e) {
            return OutData.softwareFormart("系统出错");
        }

    }

    @Override
    public Object updateMyInfo(Woker woker) {
        try{
            if (!woker.getTel().matches("\\d{11}"))
                return OutData.softwareFormart("电话号码格式不正确");
            List<HashMap> data = loginMapper.get_myInfo((Map) woker);
            if (data.isEmpty())
                return OutData.softwareFormart("该用户不存在，请联系管理员");
            loginMapper.update_tel(woker);
            return OutData.softwareFormart_OK();
        }catch (Exception e){
            return OutData.softwareFormart("网络异常");
        }
    }
}

