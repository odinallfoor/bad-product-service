package com.badexample.productservice.application.exception;

public class ProductNotFoundException extends ProductServiceException{

    private final Long productId;

    public ProductNotFoundException(String message, Long id) {
        super(message);
        this.productId = id;
    }

    public Long getProductId() {
        return productId;
    }
}
