package com.badexample.productservice.application.service;

import com.badexample.productservice.application.exception.ProductNotFoundException;
import com.badexample.productservice.domain.model.Product;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
public class ProductApplicationServiceIntegrationTest {

    @Autowired
    private ProductApplicationService service;

    @Nested
    class CreateProduct{

        @Test
        void createProduct_whenCreate_shouldPersistAndReturnProduct(){

            Product toCreate = Product.of("Cafe",
                    "Cafe en grano 500g.",
                    new BigDecimal("4990.00"),
                    10
            );

            Product created = service.createProduct(toCreate);

            assertNotNull(created.getId());

            Product found = service.getById(created.getId())
                    .orElseThrow(() -> new AssertionError("Producto no encontrado despues de la creacion."));

            assertEquals("Cafe", found.getName());
            assertEquals(0, found.getPrice().compareTo(new BigDecimal("4990.00")));
            assertEquals(10, found.getStock());

        }

        @Test
        void createProduct_whenCreateWithSameId_shouldReturnError(){

            Product created = service.createProduct(Product.of(
                    "Galleta",
                    "Galleta pequeña.",
                    new BigDecimal("1200.00"),
                    1
            ));

            service.createProduct(created);

            assertThrows(NullPointerException.class, ()-> service.createProduct(created));
        }
    }

    @Nested
    class SearchProducts{

        @Test
        void searchByName_whenMatch_shouldReturnMatchingProducts(){

            service.createProduct(Product.of("Leche Chocolate",
                    "Leche sabor chocolate 1 Lt.",
                    new BigDecimal("1800.00"),
                    5
            ));
            service.createProduct(Product.of("Leche Frutilla",
                    "Leche sabor frutilla 1 Lt.",
                    new BigDecimal("1850.00"),
                    7
            ));
            service.createProduct(Product.of("Harina",
                    "Harina sin polvos de hornear 500 gr.",
                    new BigDecimal("1800.00"),
                    5
            ));

            List<Product> productsFound = service.searchByName("Leche Frutilla");

            assertFalse(productsFound.isEmpty());
            assertEquals(1,productsFound.size());

            assertTrue(productsFound.stream().anyMatch(p -> p.getName().equals("Leche Frutilla")));
            assertTrue(productsFound.stream().noneMatch(p -> p.getName().equals("Leche Chocolate")));
            assertTrue(productsFound.stream().noneMatch(p -> p.getName().equals("Harina")));
        }

        @Test
        void searchByName_whenNoMatch_shouldReturnEmptyList(){

            Product toCreate = Product.of("Cafe",
                    "Tarro de Cafe.",
                    new BigDecimal("500.00"),
                    1
            );

            service.createProduct(toCreate);

            List<Product> productsListFound = service.searchByName("NoExiste");

            assertTrue(productsListFound.isEmpty());
        }

        @Test
        void searchAllProducts_shouldReturnAllProducts(){

            service.createProduct(Product.of("Huevos",
                    "Producto de prueba.",
                    new BigDecimal("6700.00"),
                    5
            ));
            service.createProduct(Product.of("Levadura",
                    "Producto de prueba.",
                    new BigDecimal("1850.00"),
                    7
            ));
            service.createProduct(Product.of("Harina",
                    "Producto de prueba.",
                    new BigDecimal("1800.00"),
                    5
            ));
            service.createProduct(Product.of("Mantequilla",
                    "Producto de prueba.",
                    new BigDecimal("3200.00"),
                    3
            ));

            List<Product> productList = service.searchAllProducts();

            assertEquals(productList.size(), 4);
            assertTrue(productList.stream().anyMatch(p -> p.getName().equals("Huevos")));
            assertTrue(productList.stream().anyMatch(p -> p.getName().equals("Levadura")));
            assertTrue(productList.stream().anyMatch(p -> p.getName().equals("Harina")));
            assertTrue(productList.stream().anyMatch(p -> p.getName().equals("Mantequilla")));
        }

        @Test
        void getById_whenProductNotExist_shouldReturnEmptuOptional(){

            Optional<Product> found = service.getById(99999L);

            assertTrue(found.isEmpty());
        }
    }

    @Nested
    class UpdateProduct {


        @Test
        void updateProduct_whenExistProduct_shouldPersistChanges(){

            Product toCreate = Product.of("Leche",
                    "Leche Entera sin Lactosa 1000 ml.",
                    new BigDecimal("1290.00"),
                    3
            );

            Product created = service.createProduct(toCreate);

            Product toUpdate = created.withUpdated("Leche sin Lactosa",
                    "Leche entera sin lactosa 1000 ml.",
                    new BigDecimal("1390.00"),
                    5
            );

            Product updated = service.updateProduct(toUpdate);

            assertNotNull(updated.getId());

            Optional<Product> found = service.getById(updated.getId());

            Product product = found.orElseThrow(
                    ()-> new AssertionError("Producto no encontrado despues de la actualizacion"));

            assertEquals("Leche sin Lactosa", product.getName());
            assertEquals("Leche entera sin lactosa 1000 ml.", product.getDescription());
            assertEquals(new BigDecimal("1390.00"), product.getPrice());
            assertEquals(5, product.getStock());
        }

        @Test
        void updateProduct_whenUpdate_shouldKeepSameId(){
            Product toCreate = service.createProduct(Product.of("Coca Cola",
                    "Producto de prueba.",
                    new BigDecimal("1200.00"),
                    6
            ));

            Product toUpdate = toCreate.withUpdated("Pepsi",
                    "Refresco de sabor Cola.",
                    new BigDecimal("1350"),
                    8);

            Product updated = service.updateProduct(toUpdate);

            assertEquals(toCreate.getId(), updated.getId());
        }

        @Test
        void updateProduct_whenProductNotExist_shouldThrowProductNotFoundException(){

            Product created = service.createProduct(Product.of(
                    "Producto Falso",
                    "Producto no existe, utilizado para pruebas.",
                    new BigDecimal("500.00"),
                    1
            ));

            assertNotNull(created.getId());
            service.deleteProductById(created.getId());

            Product toUpdate = created.withUpdated("Mismo Producto falso",
                    "Producto previamente eliminado.",
                    new BigDecimal("1000.00"),
                    2);


            assertThrows(ProductNotFoundException.class, ()-> service.updateProduct(toUpdate));
        }
    }

    @Nested
    class DeleteProduct {

        @Test
        void deleteProductById_whenExistProduct_shouldRemoveProduct(){

            Product toCreate = Product.of("Harina",
                    "Harina sin polvos de hornear 500 gr.",
                    new BigDecimal("1500.00"),
                    15
            );

            Product created = service.createProduct(toCreate);

            assertNotNull(created.getId());

            service.deleteProductById(created.getId());

            Optional<Product> found = service.getById(created.getId());

            assertTrue(found.isEmpty());
            assertThrows(ProductNotFoundException.class, ()-> service.deleteProductById(created.getId()));
        }

        @Test
        void deleteProductById_whenProductNotExist_shouldThrowProductNotFoundException(){

            assertThrows(ProductNotFoundException.class, ()-> service.deleteProductById(99999L));
        }
    }
}
