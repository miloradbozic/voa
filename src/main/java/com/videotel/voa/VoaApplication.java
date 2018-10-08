package com.videotel.voa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VoaApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoaApplication.class, args);
    }
}
