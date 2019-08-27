package com.mycompany.qlzf_hous_keeper;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication()
@MapperScan("com.mycompany.qlzf_hous_keeper.mapper")
@EnableTransactionManagement//开启事务管理
@ServletComponentScan
public class QlzfHousKeeperApplication {
    private static Logger logger = LoggerFactory.getLogger(QlzfHousKeeperApplication.class);

    public static void main(String[] args) {
        logger.info("test logging...");
        SpringApplication.run(QlzfHousKeeperApplication.class, args);
    }

}
