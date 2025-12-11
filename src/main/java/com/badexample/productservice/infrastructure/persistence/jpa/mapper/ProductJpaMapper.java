package com.badexample.productservice.infrastructure.persistence.jpa.mapper;

import com.badexample.productservice.domain.model.Product;
import com.badexample.productservice.infrastructure.persistence.jpa.ProductJpaEntity;

public final class ProductJpaMapper {

    private ProductJpaMapper(){}

    public static ProductJpaEntity toEntity(Product product){

        ProductJpaEntity jpaEntity = new ProductJpaEntity();
        jpaEntity.setId(product.getId());
        jpaEntity.setName(product.getName());
        jpaEntity.setDescription(product.getDescription());
        jpaEntity.setPrice(product.getPrice());
        jpaEntity.setStock(product.getStock());

        return jpaEntity;
    }

    public static Product toDomain(ProductJpaEntity productJpaEntity){

        return Product.of(
                productJpaEntity.getId(),
                productJpaEntity.getName(),
                productJpaEntity.getDescription(),
                productJpaEntity.getPrice(),
                productJpaEntity.getStock()
        );
    }
}
