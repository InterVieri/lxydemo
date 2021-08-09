package com.inter.win.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "config")
@PropertySource(value = {"classpath:config.properties"})
@Data
public class Config {
    private Integer batchOperNum;
}
