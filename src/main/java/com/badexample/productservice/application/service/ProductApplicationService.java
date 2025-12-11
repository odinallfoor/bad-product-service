package com.badexample.productservice.application.service;

import com.badexample.productservice.application.exception.ProductNotFoundException;
import com.badexample.productservice.application.port.in.ProductUseCase;
import com.badexample.productservice.application.port.out.ProductPersistencePort;
import com.badexample.productservice.domain.model.Product;

import java.util.List;
import java.util.Optional;

public class ProductApplicationService implements ProductUseCase {

    private final ProductPersistencePort persistencePort;

    public ProductApplicationService(ProductPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Product createProduct(Product product) {

        return persistencePort.save(product);
    }

    @Override
    public List<Product> searchAllProducts() {
        return persistencePort.findAll();
    }

    @Override
    public Optional<Product> getById(Long id) {

        return persistencePort.findById(id);
    }

    @Override
    public void deleteProductById(Long id) {

        Product existente = persistencePort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado.",id));

        persistencePort.deleteById(existente.getId());
    }

    @Override
    public Product updateProduct(Product product) {

        Optional<Product> productoActual = persistencePort.findById(product.getId());

        Product productoExistente = productoActual.orElseThrow(
                () -> new ProductNotFoundException("Producto no encontrado.", product.getId()));

        Product productoActualizado = productoExistente.withUpdated(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );

        return persistencePort.save(productoActualizado);
    }

    @Override
    public List<Product> searchByName(String name) {
        return persistencePort.findByName(name);
    }
}
