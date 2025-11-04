package com.badexample.productservice.controller;

import com.badexample.productservice.model.Product;
import com.badexample.productservice.repository.ProductRepository;
import com.badexample.productservice.service.ProductService;
import com.badexample.productservice.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Cosas feas intencionales:
 * - Controller conoce repositorio y servicio al mismo tiempo.
 * - No hay versionado en la ruta ("/api/products" a pelo).
 * - No hay seguridad.
 * - No hay DTOs ni mapeadores.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // field injection otra vez
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Product body) {
        try {
            // validación duplicada: controller valida + service valida
            ValidationUtils.validateProduct(body);
            Product created = productService.createProduct(body);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e){
            // devolvemos error 200 con mensaje de error en el body,
            // lo cual es TERRIBLE en APIs REST
            return ResponseEntity.ok("Error creando producto: " + e.getMessage());
        }
    }

    // READ ALL or SEARCH (dos comportamientos en el mismo endpoint)
    @GetMapping
    public ResponseEntity<List<Product>> list(@RequestParam(value = "q", required = false) String query){
        if (query != null && !query.isBlank()){
            return ResponseEntity.ok(productService.search(query));
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOne(@PathVariable Long id){
        try {
            Product p = productService.getProductById(id); // esto puede lanzar
            return ResponseEntity.ok(p);
        } catch (Exception e){
            // devolvemos 404? no... devolvemos 200 con string 🙃
            return ResponseEntity.ok("No encontrado: " + e.getMessage());
        }
    }

    // UPDATE (PUT completo)
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody Product body){
        try {
            // ni siquiera verificamos que body.id == id, se pisa igual
            ValidationUtils.validateProduct(body);
            Product updated = productService.updateProduct(id, body);
            return ResponseEntity.ok(updated);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar: " + e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        try {
            // acá saltamos el service y vamos DIRECTO al repo,
            // rompiendo la capa de servicio y creando inconsistencia
            productRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo eliminar: " + e.getMessage());
        }
    }

    // Endpoint extra que expone lógica de dominio interna sin mucho sentido:
    @GetMapping("/{id}/price-with-tax")
    public ResponseEntity<Object> getPriceWithTax(@PathVariable Long id){
        Product p = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No existe producto con id=" + id)
        );
        // devolvemos un número suelto, sin tipo, sin DTO
        return ResponseEntity.ok(p.getPriceWithTax());
    }
}
