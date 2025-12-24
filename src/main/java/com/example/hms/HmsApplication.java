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


/*
# localde
kodları değiştir
git status
git add .
git commit -m "Appointment validation improved"
git push




Localde değişiklik yap
↓
git add .
git commit -m "..."
git push
↓
Deploy platformu otomatik build eder
↓
Site güncellenir


 */