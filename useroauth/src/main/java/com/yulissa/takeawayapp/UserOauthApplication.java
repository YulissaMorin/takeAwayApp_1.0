package com.yulissa.takeawayapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * @author yulissa
 */
@EnableCaching
@EnableScheduling
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.yulissa.takeawayapp.dao.useroauth")
public class UserOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserOauthApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {return new RestTemplate();}

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Queue queue() {
        return new Queue("register_backup_queue");
    }

}
