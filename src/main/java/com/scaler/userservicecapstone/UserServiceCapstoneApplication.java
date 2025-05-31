package com.scaler.userservicecapstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UserServiceCapstoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceCapstoneApplication.class, args);
    }

}
