package com.badexample.productservice.application.port.in;

import com.badexample.productservice.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductUseCase {

    Product createProduct(Product product);
    List<Product> searchAllProducts();
    Optional<Product> getById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(Product product);
    List<Product> searchByName(String name);
}
