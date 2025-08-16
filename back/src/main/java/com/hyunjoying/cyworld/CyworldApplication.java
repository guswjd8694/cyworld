package com.hyunjoying.cyworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CyworldApplication {

    public static void main(String[] args) {
        SpringApplication.run(CyworldApplication.class, args);
    }

}
