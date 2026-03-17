package com.badexample.productservice.infrastructure.web.controller;

import com.badexample.productservice.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import com.badexample.productservice.infrastructure.web.dto.ApiErrorResponse;
import com.badexample.productservice.infrastructure.web.dto.CreateProductRequest;
import com.badexample.productservice.infrastructure.web.dto.ProductResponse;
import com.badexample.productservice.infrastructure.web.dto.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductRestControllerIntegrationTest {

    @LocalServerPort
    int port;

    String baseUrl;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ProductJpaRepository productJpaRepository;


    @BeforeEach
    void cleanDataBase(){
        productJpaRepository.deleteAll();
        baseUrl = "http://localhost:" + port + "/api/products";
    }

    @Nested
    class CreateProduct{

        @Test
        void whenValidRequest_shouldReturn201WithBody(){

            // Arrange
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Cafe");
            request.setDescription("Cafe en grano 500g.");
            request.setPrice(new BigDecimal("4990.00"));
            request.setStock(10);


            // Act
            ResponseEntity<ProductResponse> response = restTemplate.postForEntity(
                    baseUrl,
                    request,
                    ProductResponse.class);


            // Assert : Status
            assertEquals(HttpStatus.CREATED, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ProductResponse created = response.getBody();


            // Assert : Content
            assertNotNull(created.id(), "El id deberia venir asignado");
            assertEquals("Cafe", created.name());
            assertEquals("Cafe en grano 500g.", created.description());
            assertEquals(0, created.price().compareTo(new BigDecimal("4990.00")));
            assertEquals(10, created.stock());


            // Assert: Location Header
            HttpHeaders headers = response.getHeaders();
            URI location = headers.getLocation();
            assertNotNull(location, "Location header deberia existir");
            assertTrue(location.getPath().startsWith("/api/products/"), "Location deberia existir con /api/products/");
            assertTrue(location.getPath().endsWith("/" + created.id()), "Location deberia terminar con el iid creado");
        }

        @Test
        void whenInvalidRequest_shouldReturn400WithApiErrorResponse(){

            // Arrange
            CreateProductRequest request = new CreateProductRequest();
            request.setName("   ");
            request.setDescription("desc");
            request.setPrice(null);
            request.setStock(-1);


            // Act
            ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(
                    baseUrl,
                    request,
                    ApiErrorResponse.class);


            // Assert: Status
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());


            // Assert: Body
            assertNotNull(response.getBody(), "Body no deberia ser nulo");
            ApiErrorResponse body = response.getBody();


            // Assert : Content
            assertEquals("Validacion Fallida", body.getMessage());
            assertEquals("Solicitud invalida", body.getError());
            assertEquals("/api/products", body.getPath());

        }
    }

    @Nested
    class FindById{

        @Test
        void whenNotFound_shouldReturn404WithApiErrorResponse(){

            // Arrange
            Long id = 1L;

            // Act
            ResponseEntity<ApiErrorResponse> response = restTemplate.getForEntity(
                    baseUrl + "/{id}",
                    ApiErrorResponse.class,
                    id);

            // Assert : Status
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ApiErrorResponse body = response.getBody();


            // Assert : Content
            assertEquals("No encontrado", body.getError());
            assertEquals("Producto no encontrado.", body.getMessage());
            assertEquals("/api/products/" + id, body.getPath());

        }

        @Test
        void whenValidRequest_shouldReturnProductResponse(){

            // Arrange
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Cafe");
            request.setDescription("Cafe en grano 500g.");
            request.setPrice(new BigDecimal("4990.00"));
            request.setStock(10);
            ResponseEntity<ProductResponse> created = restTemplate.postForEntity(
                    baseUrl,
                    request,
                    ProductResponse.class);


            // Act
            ResponseEntity<ProductResponse> response = restTemplate.getForEntity(
                    baseUrl + "/{id}",
                    ProductResponse.class,
                    created.getBody().id());


            // Assert : Status
            assertEquals(HttpStatus.OK, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ProductResponse body = response.getBody();


            // Assert : Content
            assertEquals(created.getBody().id(), body.id());
            assertEquals(created.getBody().name(), body.name());
            assertEquals(created.getBody().description(), body.description());
            assertEquals(0, created.getBody().price().compareTo(body.price()));
            assertEquals(created.getBody().stock(), body.stock());
        }
    }

    @Nested
    class Search{

        @Test
        void whenSearchAllProducts_shouldReturnAllCreatedProducts(){

            // Arrange
            CreateProductRequest productOne = new CreateProductRequest();
            productOne.setName("Leche");
            productOne.setDescription("Leche en caja 1 Lt.");
            productOne.setPrice(new BigDecimal("1200.00"));
            productOne.setStock(1);
            ResponseEntity<ProductResponse> createdOne = restTemplate.postForEntity(
                    baseUrl,
                    productOne,
                    ProductResponse.class);

            CreateProductRequest productTwo = new CreateProductRequest();
            productTwo.setName("Leche Chocolate");
            productTwo.setDescription("Leche sabor chocolate en caja 1 Lt.");
            productTwo.setPrice(new BigDecimal("1350.00"));
            productTwo.setStock(2);
            ResponseEntity<ProductResponse> createdTwo = restTemplate.postForEntity(
                    baseUrl,
                    productTwo,
                    ProductResponse.class);

            CreateProductRequest productThree = new CreateProductRequest();
            productThree.setName("Leche Frutilla");
            productThree.setDescription("Leche sabor frutilla en caja 1 Lt.");
            productThree.setPrice(new BigDecimal("1330.00"));
            productThree.setStock(3);
            ResponseEntity<ProductResponse> createdThree = restTemplate.postForEntity(
                    baseUrl,
                    productThree,
                    ProductResponse.class);


            // Act
            ResponseEntity<ProductResponse[]> response = restTemplate.getForEntity(
                    baseUrl,
                    ProductResponse[].class);


            // Assert : Status
            assertEquals(HttpStatus.OK, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ProductResponse[] body = response.getBody();
            assertEquals(3, body.length);


            // Assert : Content
            assertNotNull(createdOne.getBody(), "El primer producto no tiene body.");
            assertNotNull(createdTwo.getBody(), "El segundo producto no tiene body.");
            assertNotNull(createdThree.getBody(), "El tercer producto no tiene body.");

            var names = Arrays.stream(body).map(ProductResponse::name).toList();
            assertTrue(names.contains(createdOne.getBody().name()));
            assertTrue(names.contains(createdTwo.getBody().name()));
            assertTrue(names.contains(createdThree.getBody().name()));

            var ids = Arrays.stream(body).map(ProductResponse::id).toList();
            assertTrue(ids.contains(createdOne.getBody().id()));
            assertTrue(ids.contains(createdTwo.getBody().id()));
            assertTrue(ids.contains(createdThree.getBody().id()));

        }

        @Test
        void whenSearchByName_shouldReturnMatchingProducts(){

            // Arrange
            CreateProductRequest productOne = new CreateProductRequest();
            productOne.setName("Harina");
            productOne.setDescription("Saco que harina 500g.");
            productOne.setPrice(new BigDecimal("1500.00"));
            productOne.setStock(1);
            ResponseEntity<ProductResponse> createdOne = restTemplate.postForEntity(
                    baseUrl,
                    productOne,
                    ProductResponse.class);

            CreateProductRequest productTwo = new CreateProductRequest();
            productTwo.setName("Leche Chocolate");
            productTwo.setDescription("Leche sabor chocolate en caja 1 Lt.");
            productTwo.setPrice(new BigDecimal("1350.00"));
            productTwo.setStock(2);
            ResponseEntity<ProductResponse> createdTwo = restTemplate.postForEntity(
                    baseUrl,
                    productTwo,
                    ProductResponse.class);

            CreateProductRequest productThree = new CreateProductRequest();
            productThree.setName("Leche Frutilla");
            productThree.setDescription("Leche sabor frutilla en caja 1 Lt.");
            productThree.setPrice(new BigDecimal("1330.00"));
            productThree.setStock(3);
            ResponseEntity<ProductResponse> createdThree = restTemplate.postForEntity(
                    baseUrl,
                    productThree,
                    ProductResponse.class);

            String matchPhrase = "Leche Frutilla";


            // Act
            ResponseEntity<ProductResponse[]> response = restTemplate.getForEntity(
                    baseUrl + "?q={q}",
                    ProductResponse[].class,
                    matchPhrase);


            // Assert : Status
            assertEquals(HttpStatus.OK, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ProductResponse[] body = response.getBody();
            assertEquals(1, body.length);


            // Assert : Content
            assertNotNull(createdOne.getBody(), "El primer producto no tiene body.");
            assertNotNull(createdTwo.getBody(), "El segundo producto no tiene body.");
            assertNotNull(createdThree.getBody(), "El tercer producto no tiene body.");

            var names = Arrays.stream(body).map(ProductResponse::name).toList();
            assertTrue(names.contains(createdThree.getBody().name()));
            assertFalse(names.contains(createdOne.getBody().name()));
            assertFalse(names.contains(createdTwo.getBody().name()));

            var ids = Arrays.stream(body).map(ProductResponse::id).toList();
            assertTrue(ids.contains(createdThree.getBody().id()));
            assertFalse(ids.contains(createdOne.getBody().id()));
            assertFalse(ids.contains(createdTwo.getBody().id()));

        }

    }

    @Nested
    class UpdateProduct{

        @Test
        void whenValidRequest_shouldUpdateAndReturnProductResponse(){

            // Arrange
            CreateProductRequest product = new CreateProductRequest();
            product.setName("Harina");
            product.setDescription("Saco que harina 500g.");
            product.setPrice(new BigDecimal("1500.00"));
            product.setStock(1);

            ResponseEntity<ProductResponse> created = restTemplate.postForEntity(
                    baseUrl,
                    product,
                    ProductResponse.class);

            Long id = created.getBody().id();

            UpdateProductRequest request = new UpdateProductRequest();
            request.setName("Doritos");
            request.setDescription("Doritos tradicional 280g");
            request.setPrice(new BigDecimal("2500"));
            request.setStock(20);


            // Act
            ResponseEntity<ProductResponse> response = restTemplate.exchange(
                    baseUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(request),
                    ProductResponse.class,
                    id);


            // Assert : Status
            assertEquals(HttpStatus.OK, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ProductResponse body = response.getBody();


            // Assert : Content
            assertEquals(id, body.id());
            assertEquals(request.getName(), body.name());
            assertEquals(request.getDescription(), body.description());
            assertEquals(0, request.getPrice().compareTo(body.price()));
            assertEquals(request.getStock(), body.stock());

            assertNotEquals(created.getBody().name(), body.name());
            assertNotEquals(created.getBody().description(), body.description());
            assertNotEquals(created.getBody().price(), body.price());
            assertNotEquals(created.getBody().stock(), body.stock());
        }

        @Test
        void whenInvalidRequest_shouldReturn400WithApiErrorResponse(){

            // Arrange
            CreateProductRequest product = new CreateProductRequest();
            product.setName("Pan de Molde.");
            product.setDescription("Pan de Molde Original 850g.");
            product.setPrice(new BigDecimal("2000.00"));
            product.setStock(300);

            ResponseEntity<ProductResponse> created = restTemplate.postForEntity(
                    baseUrl,
                    product,
                    ProductResponse.class);

            Long id = created.getBody().id();

            UpdateProductRequest request = new UpdateProductRequest();
            request.setName("  ");
            request.setDescription("Producto No Valido.");
            request.setPrice(null);
            request.setStock(-20);


            // Act
            ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
                    baseUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(request),
                    ApiErrorResponse.class,
                    id);


            // Assert : Status
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ApiErrorResponse body = response.getBody();


            // Assert : Content
            assertEquals("Validacion Fallida", body.getMessage());
            assertEquals("Solicitud invalida", body.getError());
            assertEquals("/api/products/" + id, body.getPath());
            assertNotNull(body.getDetails());
        }

        @Test
        void whenProductNotFound_shouldReturn404WithApiErrorResponse(){

            // Arrange
            Long id = 999L;

            UpdateProductRequest request = new UpdateProductRequest();
            request.setName("Doritos");
            request.setDescription("Doritos tradicional 200g");
            request.setPrice(new BigDecimal("1500"));
            request.setStock(10);


            // Act
            ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
                    baseUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(request),
                    ApiErrorResponse.class,
                    id);


            // Assert : Status
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ApiErrorResponse body = response.getBody();


            // Assert : Content
            assertEquals("No encontrado", body.getError());
            assertEquals("Producto no encontrado.", body.getMessage());
            assertEquals("/api/products/" + id, body.getPath());
            assertNotNull(body.getDetails());

        }
    }

    @Nested
    class DeleteProduct{

        @Test
        void whenValidRequest_shouldReturn204NoContent(){

            // Arrange
            CreateProductRequest product = new CreateProductRequest();
            product.setName("Hielo");
            product.setDescription("Bolsa de Hielo 1kg.");
            product.setPrice(new BigDecimal("1000.00"));
            product.setStock(30);

            ResponseEntity<ProductResponse> created = restTemplate.postForEntity(
                    baseUrl,
                    product,
                    ProductResponse.class);

            Long id = created.getBody().id();


            // Act
            ResponseEntity<Void> response = restTemplate.exchange(
                    baseUrl + "/{id}",
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    Void.class,
                    id);


            // Assert : Status
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        void whenProductNotFound_shouldReturn404WithApiErrorResponse(){

            // Arrange
            Long id = 999L;


            // Act
            ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
                    baseUrl + "/{id}",
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    ApiErrorResponse.class,
                    id);


            // Assert : Status
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ApiErrorResponse body = response.getBody();


            // Assert : Content
            assertEquals("No encontrado", body.getError());
            assertEquals("Producto no encontrado.", body.getMessage());
            assertEquals("/api/products/" + id, body.getPath());
            assertNotNull(body.getDetails());
        }
    }

    @Nested
    class PriceWithTax{

        @Test
        void whenValidRequest_shouldReturnPriceWithTax(){

            // Arrange
            CreateProductRequest product = new CreateProductRequest();
            product.setName("Chocolate");
            product.setDescription("Barra de Chocolate 100g");
            product.setPrice(new BigDecimal("1000.00"));
            product.setStock(10);

            ResponseEntity<ProductResponse> created = restTemplate.postForEntity(
                    baseUrl,
                    product,
                    ProductResponse.class);

            Long id = created.getBody().id();
            BigDecimal tax = new BigDecimal("0.19");
            BigDecimal expected = created.getBody().price().multiply(BigDecimal.ONE.add(tax));


            // Act
            ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
                    baseUrl + "/{id}/price-with-tax?taxRate={tax}",
                    BigDecimal.class,
                    id,
                    tax);


            // Assert : Status
            assertEquals(HttpStatus.OK, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            BigDecimal body = response.getBody();


            // Assert : Content
            assertEquals(0, expected.compareTo(body));
        }

        @Test
        void whenMissingTaxRate_shouldReturn400WithApiErrorResponse(){

            // Arrange
            CreateProductRequest product = new CreateProductRequest();
            product.setName("Chocolate");
            product.setDescription("Barra de Chocolate 100g");
            product.setPrice(new BigDecimal("1000.00"));
            product.setStock(15);

            ResponseEntity<ProductResponse> created = restTemplate.postForEntity(
                    baseUrl,
                    product,
                    ProductResponse.class);

            Long id = created.getBody().id();


            // Act
            ResponseEntity<ApiErrorResponse> response = restTemplate.getForEntity(
                    baseUrl + "/{id}/price-with-tax",
                    ApiErrorResponse.class,
                    id);


            // Assert : Status
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ApiErrorResponse body = response.getBody();


            // Assert : Content
            assertEquals("Solicitud invalida", body.getError());
            assertEquals("Falta parametro requerido", body.getMessage());
            assertEquals("/api/products/" + id + "/price-with-tax", body.getPath());
            assertNotNull(body.getDetails());
        }

        @Test
        void whenTaxRateIsNegative_shouldReturn400WithApiErrorResponse(){

            // Arrange
            CreateProductRequest product = new CreateProductRequest();
            product.setName("Chocolate");
            product.setDescription("Barra de Chocolate 100g");
            product.setPrice(new BigDecimal("1500.00"));
            product.setStock(20);

            ResponseEntity<ProductResponse> created = restTemplate.postForEntity(
                    baseUrl,
                    product,
                    ProductResponse.class);

            Long id = created.getBody().id();
            BigDecimal tax = new BigDecimal("-0.10");


            // Act
            ResponseEntity<ApiErrorResponse> response = restTemplate.getForEntity(
                    baseUrl + "/{id}/price-with-tax?taxRate={tax}",
                    ApiErrorResponse.class,
                    id,
                    tax);


            // Assert : Status
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ApiErrorResponse body = response.getBody();


            // Assert : Content
            assertEquals("Solicitud invalida", body.getError());
            assertEquals("La tasa de impuesto no puede ser negativa.", body.getMessage());
            assertEquals("/api/products/" + id + "/price-with-tax", body.getPath());
        }

        @Test
        void whenProductNotFound_shouldReturn404WithApiErrorResponse(){

            // Arrange
            Long id = 999L;
            BigDecimal taxRate = new BigDecimal("0.19");


            // Act
            ResponseEntity<ApiErrorResponse> response = restTemplate.getForEntity(
                    baseUrl + "/{id}/price-with-tax?taxRate={taxRate}",
                    ApiErrorResponse.class,
                    id,
                    taxRate);

            // Assert : Status
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());


            // Assert : Body
            assertNotNull(response.getBody(), "El body no deberia ser null");
            ApiErrorResponse body = response.getBody();


            // Assert : Content
            assertEquals("No encontrado", body.getError());
            assertEquals("Producto no encontrado.", body.getMessage());
            assertEquals("/api/products/" + id + "/price-with-tax", body.getPath());
            assertNotNull(body.getDetails());
        }
    }
}