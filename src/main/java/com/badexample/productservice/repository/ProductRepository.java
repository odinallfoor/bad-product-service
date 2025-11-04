package com.badexample.productservice.repository;

import com.badexample.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Mala práctica: esta interfaz queda pública y la usamos directamente desde el Controller.
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // consulta por nombre CONTAINS sin paginación ni rate limit
    List<Product> findByNameContainingIgnoreCase(String name);
}
