package com.cykj.psychology;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.cykj.psychology.mapper")
@SpringBootApplication
public class PsychologySystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(PsychologySystemApplication.class, args);
    }
}