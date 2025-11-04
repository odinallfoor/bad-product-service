package com.badexample.productservice.util;

import com.badexample.productservice.model.Product;

public class ValidationUtils {

    // método estático global = súper acoplamiento
    public static void validateProduct(Product p) {
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio (validación Utils)");
        }
        if (p.getPrice() < 0) {
            throw new RuntimeException("El precio no puede ser negativo (validación Utils)");
        }
    }
}
