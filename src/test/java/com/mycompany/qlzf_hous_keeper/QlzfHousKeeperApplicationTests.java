package com.mycompany.qlzf_hous_keeper;

import com.mycompany.qlzf_hous_keeper.config.redis.RedisUtil;
import com.mycompany.qlzf_hous_keeper.mapper.TextMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QlzfHousKeeperApplicationTests {



    @Test
    public void contextLoads() {
//        HashMap map_sql = new HashMap();
//        List<HashMap<String, String>> idList = textMapper.getShopId();
//        for (HashMap eachIdList:idList) {
//            String shop_id = eachIdList.get("id").toString();
//            List<HashMap<String, String>> telRes = textMapper.getWorkerTel(map_sql);
//            redisUtil.set("wntel" + shop_id, telRes.get(0).get("tel"));
//            System.out.println("id:"+shop_id+"is ok");
        }
    }


