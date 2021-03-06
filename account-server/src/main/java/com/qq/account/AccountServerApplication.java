package com.qq.account;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description: AccountServerApplication
 * @Author QinQiang
 * @Date 2022/4/28
 **/
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan({"com.qq.**.mapper"})
public class AccountServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(AccountServerApplication.class, args);
    }
}
