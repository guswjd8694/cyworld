package com.hyunjoying.cyworld;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class CyworldApplication {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        System.out.println("====== 버전 체크: 진짜 바뀐거 맞지 ======");
        SpringApplication.run(CyworldApplication.class, args);
    }

}
