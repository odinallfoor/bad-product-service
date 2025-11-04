package com.badexample.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {

    // Esta clase tiene dos responsabilidades: arranque y además registra data de ejemplo.
    // (mala práctica SRP)
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
        System.out.println("Product Service is running with ALL the smells 🧟");
    }
}
