package com.mycompany.qlzf_hous_keeper.config.es;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/17 15:30
 */
@Component
@ConfigurationProperties(prefix = "es.server")
@Getter
@Setter
public class EsProperties {
    private String clusterName;
    private String host;
    private int transPort;
}
