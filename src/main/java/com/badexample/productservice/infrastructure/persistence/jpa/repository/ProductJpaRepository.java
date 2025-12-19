package com.badexample.productservice.infrastructure.persistence.jpa.repository;

import com.badexample.productservice.infrastructure.persistence.jpa.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    List<ProductJpaEntity> findByName(String name);
}
