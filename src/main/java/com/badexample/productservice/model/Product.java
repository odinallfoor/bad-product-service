package com.badexample.productservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // también lo vamos a permitir que venga desde el body en el update → inseguro

    @Column(nullable = false)
    private String name;

    private String description;

    private double price; // debería ser BigDecimal

    private Integer stock;

    // lógica "de negocio" incrustada en la entidad JPA
    public double getPriceWithTax() {
        return this.price * 1.19; // 19% IVA hardcodeado
    }
}
