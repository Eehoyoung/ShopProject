package com.shop.onlyfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OnlyfitApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlyfitApplication.class, args);
    }

}
