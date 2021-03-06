package com.qq.gateway.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author QinQiang
 * @Date 2022/4/2
 **/
@RefreshScope
@Component
@Data
@ToString
public class MyConfig {
    @Value("${user.name:qwer}")
    private String name;
    @Value("${user.name:qwer}")
    private String age;
}
