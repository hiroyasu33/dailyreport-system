package com.techacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.techacademy")
public class DailyReportSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyReportSystemApplication.class, args);
    }

}
