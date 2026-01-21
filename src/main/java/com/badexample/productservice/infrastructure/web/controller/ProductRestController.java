package com.badexample.productservice.infrastructure.web.controller;

import com.badexample.productservice.application.exception.ProductNotFoundException;
import com.badexample.productservice.application.port.in.ProductUseCase;
import com.badexample.productservice.infrastructure.web.dto.CreateProductRequest;
import com.badexample.productservice.infrastructure.web.dto.ProductResponse;
import com.badexample.productservice.infrastructure.web.dto.UpdateProductRequest;
import com.badexample.productservice.infrastructure.web.mapper.ProductWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductUseCase productUseCase;

    public ProductRestController(ProductUseCase productUseCase){
        this.productUseCase = productUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid
                                                         @RequestBody CreateProductRequest request){

        ProductResponse created = ProductWebMapper.toResponse(
                productUseCase.createProduct(ProductWebMapper.toNewProduct(
                        request
                ))
        );

        URI location = URI.create("/api/products/" + created.id());

        return ResponseEntity
                .created(location)
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id){

        ProductResponse found = ProductWebMapper.toResponse(
                productUseCase.getById(id).orElseThrow(()->
                        new ProductNotFoundException("Producto no encontrado.", id)
                )
        );

        return ResponseEntity
                .ok(found);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findProduct(
            @RequestParam(value = "q", required = false) String q) {

        boolean hasQuery = (q != null && !q.isBlank());

        List<ProductResponse> found = (hasQuery
                ? productUseCase.searchByName(q)
                : productUseCase.searchAllProducts())
                .stream()
                .map(ProductWebMapper::toResponse)
                .toList();

        return ResponseEntity
                .ok(found);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateProductRequest request){

        ProductResponse updated = ProductWebMapper.toResponse(
                productUseCase.updateProduct(ProductWebMapper.toExistingProduct(id, request))
        );

        return ResponseEntity
                .ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){

        productUseCase.deleteProductById(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/{id}/price-with-tax")
    public ResponseEntity<BigDecimal> productWithTax(@PathVariable Long id,
                                           @RequestParam BigDecimal taxRate){

        return ResponseEntity.ok(productUseCase.getById(id)
                .orElseThrow(
                    ()-> new ProductNotFoundException("Producto no encontrado.", id))
                .priceWithTax(taxRate));
    }
}
