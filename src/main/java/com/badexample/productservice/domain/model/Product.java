package com.badexample.productservice.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Product {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final int stock;

    private Product(Long id, String name, String description, BigDecimal price, int stock) {
        this.id = id;
        this.name = requiredNonBlank(name, "El nombre es obligatorio.");
        this.description = (description == null) ? "":description.trim();
        this.price = requiredNonNegative(price, "El precio no puede ser negativo.");
        this.stock = requiredNonNegative(stock,"La cantidad no puede ser negativa.");
    }

    public static Product of(Long id, String name, String description, BigDecimal price, int stock){
        return new Product(id,name,description,price,stock);
    }

    public Product withUpdated(String name, String description, BigDecimal price, int stock){
        return new Product(this.id,name,description,price,stock);
    }

    public BigDecimal priceWithTax(BigDecimal taxRate){
        Objects.requireNonNull(taxRate, "La tasa de impuesto es obligatoria.");
        if(taxRate.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("La tasa de impuesto no puede ser negativa.");
        }
        return price.multiply(BigDecimal.ONE.add(taxRate));
    }

    private static String requiredNonBlank(String value, String message){
        if(value == null || value.trim().isEmpty()){
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static BigDecimal requiredNonNegative(BigDecimal value, String message){
        Objects.requireNonNull(value,message);
        if(value.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static int requiredNonNegative(int value, String message){
        if(value < 0){
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product other)) return false;

        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
