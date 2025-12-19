package com.badexample.productservice.infrastructure.persistence.jpa.adapter;

import com.badexample.productservice.application.port.out.ProductPersistencePort;
import com.badexample.productservice.domain.model.Product;
import com.badexample.productservice.infrastructure.persistence.jpa.ProductJpaEntity;
import com.badexample.productservice.infrastructure.persistence.jpa.mapper.ProductJpaMapper;
import com.badexample.productservice.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductJpaRepository productJpaRepository;

    public ProductPersistenceAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity productJpaEntity = ProductJpaMapper.toEntity(product);
        return ProductJpaMapper.toDomain(productJpaRepository.save(productJpaEntity));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(ProductJpaMapper::toDomain);
    }

    @Override
    public boolean existById(Long id) {
        return productJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream().map(ProductJpaMapper::toDomain).toList();
    }

    @Override
    public List<Product> findByName(String name) {
        return productJpaRepository.findByName(name).stream().map(ProductJpaMapper::toDomain).toList();
    }
}
