package com.foodielog.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.foodielog.application", "com.foodielog.server"})
@EntityScan(basePackages = "com.foodielog.server")
@EnableJpaRepositories(basePackages = "com.foodielog.server")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
