package com.example.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 🔹 Scheduler’ı aktif hale getir
public class HmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(HmsApplication.class, args);
    }
}
