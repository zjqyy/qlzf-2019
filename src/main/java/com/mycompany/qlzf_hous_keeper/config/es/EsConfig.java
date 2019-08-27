package com.mycompany.qlzf_hous_keeper.config.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/17 15:36
 */
@Configuration
public class EsConfig {
    @Autowired
    EsProperties esProperties;

    @Bean(name = "esClient")
    public TransportClient esClient() {
        TransportClient esClient = null;
        try {
            Settings settings = Settings.builder().put("cluster.name", esProperties.getClusterName())
                    .put("client.transport.sniff", true).build();
            esClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esProperties.getHost()), esProperties.getTransPort()));
        } catch (UnknownHostException uhex) {
            System.out.println("连接ES出错");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return esClient;
    }
}
