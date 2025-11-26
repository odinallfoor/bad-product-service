package com.badexample.productservice.service;

import com.badexample.productservice.model.Product;
import com.badexample.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    // inyeccion de dependencia no recomendada, se debe cambiar por inyteccion por contructor
    @Autowired
    private ProductRepository productRepository;

    // recibe directamente un jpa, cuando deberia ser un DTO
    public Product createProduct(Product p) {
        if (p.getName() == null || p.getName().isBlank()) {
            throw new RuntimeException("Nombre requerido");
        }
        // validacion repetida en el controller, deberia quedar solo aca esta validacion
        if (p.getPrice() < 0) {
            throw new RuntimeException("Precio no puede ser negativo");
        }
        // no estoy del to do seguro, pero creo que esto deberia estar en la capa 3 y esta es la 2 no?
        // otra cosa, esta devolviendo un jpa, deberia cambiarse por un DTO
        return productRepository.save(p);
    }

    // devuelve una lista de JPA, deberia devolver una lista de DTO
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    // como entrada solicita un JPA, deberia cambiarse por un DTO
    public Product getProductById(Long id) {

        return productRepository.findById(id).get();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // como entrada solicita un JPA, deberia cambiarse a un DTO
    public Product updateProduct(Long id, Product incoming) {
        Product existing = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No existe producto id=" + id)
        );


        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setPrice(incoming.getPrice());
        existing.setStock(incoming.getStock());

        return productRepository.save(existing);
    }

    // devuelve una lista de JPA, deberia ser un DTO
    public List<Product> search(String q) {
        return productRepository.findByNameContainingIgnoreCase(q);
    }
}
