package com.seekey.kpss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class KpssApplication {

    public static void main(String[] args) {
        //SpringApplication.run(KpssApplication.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(KpssApplication.class);
        builder.headless(false).run(args);
    }

}
