package com.itheima.canal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableCanalClient
public class CanalApplication<EnableCanalClient> {
    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class, args);
    }

}
