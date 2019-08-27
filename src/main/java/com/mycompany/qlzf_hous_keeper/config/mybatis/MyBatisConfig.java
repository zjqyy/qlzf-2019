package com.mycompany.qlzf_hous_keeper.config.mybatis;

import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

@org.springframework.context.annotation.Configuration
public class MyBatisConfig {


    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(Configuration configuration) {

                configuration.setMapUnderscoreToCamelCase(true);
            }
        };
    }
//    @Bean
//    public SqlSessionFactoryBean sqlSessionFactoryBean()  throws IOException {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        //设置数据源
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        //设置别名包
//        sqlSessionFactoryBean.setTypeAliasesPackage("com.mycompany.qlzf_hous_keeper.entity");
//        //添加XML目录
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        //设置扫描xml文件
//        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
//        sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:mybatis-config.xml"));
//        return sqlSessionFactoryBean;
//
//    }
//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() {
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        //设置sqlSession工厂
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
//        //设置mapper扫描包
//        mapperScannerConfigurer.setBasePackage("com.mycompany.qlzf_hous_keeper.mapper");
//        return mapperScannerConfigurer;
//    }

}
