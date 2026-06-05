package com.agrotrack.suscription.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AgrotrackSuscriptionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgrotrackSuscriptionServiceApplication.class, args);
    }

}
