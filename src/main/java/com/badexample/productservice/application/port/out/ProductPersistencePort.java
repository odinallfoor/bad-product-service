package com.badexample.productservice.application.port.out;

import com.badexample.productservice.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductPersistencePort {

    Product save(Product product);
    Optional<Product> findById(Long id);
    boolean existById(Long id);
    void deleteById(Long id);
    List<Product> findAll();
    List<Product> findByName(String name);
}
