package com.autohrsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AutoHrSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoHrSystemApplication.class, args);
    }

}
