package com.badexample.productservice.infrastructure.web.mapper;

import com.badexample.productservice.domain.model.Product;
import com.badexample.productservice.infrastructure.web.dto.CreateProductRequest;
import com.badexample.productservice.infrastructure.web.dto.ProductResponse;
import com.badexample.productservice.infrastructure.web.dto.UpdateProductRequest;

public final class ProductWebMapper {

    private ProductWebMapper(){}

    public static Product toNewProduct(CreateProductRequest request){

        return Product.of(request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock());
    }

    public static Product toExistingProduct(Long id, UpdateProductRequest request){

        return Product.rehydrate(id,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock());
    }

    public static ProductResponse toResponse(Product product){

        return new ProductResponse(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock());
    }
}
