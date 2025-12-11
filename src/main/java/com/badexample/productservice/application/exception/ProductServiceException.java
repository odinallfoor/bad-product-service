package com.badexample.productservice.application.exception;

public class ProductServiceException extends RuntimeException{

    public ProductServiceException(String message){
        super(message);
    }

    public ProductServiceException(String message, Throwable cause){
        super(message, cause);
    }

}
