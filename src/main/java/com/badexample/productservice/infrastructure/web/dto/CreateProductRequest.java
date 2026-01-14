package com.badexample.productservice.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreateProductRequest {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 60, message = "El nombre no puede tener mas de 60 caracteres.")
    private String name;

    @Size(max = 255, message = "La descripcion no puede tener mas de 255 caracteres.")
    private String description;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo.", inclusive = true)
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
