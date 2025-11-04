package com.badexample.productservice.service;

import com.badexample.productservice.model.Product;
import com.badexample.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    // mala práctica: inyección por campo en vez de constructor
    @Autowired
    private ProductRepository productRepository;

    // método dios que hace de todo
    public Product createProduct(Product p) {
        // validación pobre y duplicada (también se valida en controller)
        if (p.getName() == null || p.getName().isBlank()) {
            throw new RuntimeException("Nombre requerido");
        }
        if (p.getPrice() < 0) {
            throw new RuntimeException("Precio no puede ser negativo");
        }

        // no seteamos nada más (ni timestamps, ni auditorías)
        return productRepository.save(p);
    }

    public List<Product> getAllProducts() {
        // sin paginación => potencial DoS si hay miles
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        // get() directo sobre Optional => puede lanzar NoSuchElementException rara
        return productRepository.findById(id).get();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product updateProduct(Long id, Product incoming) {
        Product existing = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No existe producto id=" + id)
        );

        // update irresponsable: pisamos todo sin reglas de negocio claras
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setPrice(incoming.getPrice());
        existing.setStock(incoming.getStock());

        return productRepository.save(existing);
    }

    public List<Product> search(String q) {
        return productRepository.findByNameContainingIgnoreCase(q);
    }
}
