package com.badexample.productservice.domain.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class ProductTest {

    @Nested
    class Creation{

        @Test
        void of_whenDataIsOk_shouldCreateProductWithIdNull(){

            Product created = assertDoesNotThrow(() -> Product.of(
                    "Cafe",
                    "Producto invalido.",
                    new BigDecimal("100.00"),
                    1
            ));

            assertEquals("Cafe", created.getName());
            assertEquals(new BigDecimal("100.00"), created.getPrice());
            assertEquals(1, created.getStock());
            assertNull(created.getId());
        }
    }

    @Nested
    class Invariants {

        @Test
        void of_whenNameIsNull_shouldThrowIllegalArgumentException(){

            assertThrows(IllegalArgumentException.class,
                    ()-> Product.of(
                            null,
                            "Producto invalido.",
                            new BigDecimal("100.00"),
                            1
                    ));
        }

        @Test
        void of_whenNameIsBlank_shouldThrowIllegalArgumentException(){

            assertThrows(IllegalArgumentException.class,
                    ()-> Product.of(
                            " ",
                            "Producto invalido.",
                            new BigDecimal("100.00"),
                            1
                    ));
        }

        @Test
        void of_whenPriceIsNegative_shouldThrowIllegalArgumentException(){

            assertThrows(IllegalArgumentException.class,
                    ()-> Product.of(
                            "Cafe",
                            "Producto invalido.",
                            new BigDecimal("-1"),
                            1
                    ));
        }

        @Test
        void of_whenPriceIsNull_shouldThrowNullPointerException(){

            assertThrows(NullPointerException.class,
                    ()-> Product.of(
                            "Cafe",
                            "Producto invalido.",
                            null,
                            1
                    ));
        }

        @Test
        void of_whenStockIsNegative_shouldThrowIllegalArgumentException(){

            assertThrows(IllegalArgumentException.class,
                    ()-> Product.of(
                            "Cafe",
                            "Producto invalido.",
                            new BigDecimal("100.00"),
                            -1
                    ));
        }
    }

    @Nested
    class Update {

        @Test
        void withUpdate_whenDataIsOk_shouldUpdateData(){

            Product toUpdate = Product.of(
                    "Cafe",
                    "Producto de prueba.",
                    new BigDecimal("100.00"),
                    1
            );

            Product updated = assertDoesNotThrow(()->toUpdate.withUpdated(
                    "Harina",
                    "Mismo Producto de prueba.",
                    new BigDecimal("500.00"),
                    2
            ));

            assertEquals("Harina", updated.getName());
            assertEquals("Mismo Producto de prueba.", updated.getDescription());
            assertEquals(new BigDecimal("500.00"), updated.getPrice());
            assertEquals(2, updated.getStock());
        }

        @Test
        void withUpdate_whenNameIsBlankOrNull_shouldThrowIllegalArgumentException(){

            Product toUpdate = Product.of(
                    "Cafe",
                    "Producto de prueba.",
                    new BigDecimal("100.00"),
                    1
            );

            assertThrows(IllegalArgumentException.class,
                    ()->toUpdate.withUpdated(
                    " ",
                    "Mismo Producto de prueba.",
                    new BigDecimal("500.00"),
                    2
            ));

            assertThrows(IllegalArgumentException.class,
                    ()->toUpdate.withUpdated(
                            null,
                            "Mismo Producto de prueba.",
                            new BigDecimal("500.00"),
                            2
                    ));
        }

        @Test
        void withUpdate_whenPriceIsNegative_shouldThrowIllegalArgumentException(){

            Product toUpdate = Product.of(
                    "Cafe",
                    "Producto de prueba.",
                    new BigDecimal("100.00"),
                    1
            );

            assertThrows(IllegalArgumentException.class,
                    ()->toUpdate.withUpdated(
                            "Harina",
                            "Mismo Producto de prueba.",
                            new BigDecimal("-500.00"),
                            2
                    ));
        }

        @Test
        void withUpdate_whenStockIsNegative_shouldThrowIllegalArgumentException(){

            Product toUpdate = Product.of(
                    "Cafe",
                    "Producto de prueba.",
                    new BigDecimal("100.00"),
                    1
            );

            assertThrows(IllegalArgumentException.class,
                    ()->toUpdate.withUpdated(
                            "Harina",
                            "Mismo Producto de prueba.",
                            new BigDecimal("500.00"),
                            -2
                    ));
        }
    }

    @Nested
    class Pricing {

        @Test
        void priceWithTax_whenDataIdOk_shouldReturnValidObject(){

            Product toTax = Product.of(
                    "Cafe",
                    "Producto de prueba.",
                    new BigDecimal("100.00"),
                    1
            );

            BigDecimal result = assertDoesNotThrow(()-> toTax.priceWithTax(new BigDecimal("0.19")));

            assertEquals(0, result.compareTo(new BigDecimal("119.0000")));
        }

        @Test
        void priceWithTax_whenTaxIsNull_shouldThrowNullPointerException(){

            Product toTax = Product.of(
                    "Cafe",
                    "Producto de prueba.",
                    new BigDecimal("100.00"),
                    1
            );

            assertThrows(NullPointerException.class,
                    ()-> toTax.priceWithTax(null));
        }

        @Test
        void priceWithTax_whenTaxIsNegative_shouldThrowIllegalArgumentException(){

            Product toTax = Product.of(
                    "Cafe",
                    "Producto de prueba.",
                    new BigDecimal("100.00"),
                    1
            );

            assertThrows(IllegalArgumentException.class,
                    ()-> toTax.priceWithTax(new BigDecimal("-0.19")));
        }

        @Test
        void priceWithTax_whenTaxIsZero_ShouldReturnSamePrice(){

            Product toTax = Product.of(
                    "Chocolate",
                    "Producto de prueba.",
                    new BigDecimal("1000.00"),
                    15
            );

            assertEquals(0, toTax.priceWithTax(new BigDecimal("0")).compareTo(new BigDecimal("1000")));
        }
    }

    @Nested
    class Normalization {

        @Test
        void of_whenDescripcionIsNull_shouldTranformToBlank(){

            Product product = Product.of(
                    "Cafe",
                    null,
                    new BigDecimal("100.00"),
                    1
            );

            assertEquals("",product.getDescription());
        }

        @Test
        void of_whenNameHasSpaces_shouldTrim(){

            Product product = Product.of(
                    " Cafe ",
                    "Producto con espacios.",
                    new BigDecimal("100.00"),
                    1
            );

            assertEquals("Cafe", product.getName());
        }

        @Test
        void of_whenDescriptionHasSpaces_shouldTrim(){

            Product product = Product.of(
                    "Cafe",
                    "     Producto con espacios.    ",
                    new BigDecimal("100.00"),
                    1
            );

            assertEquals("Producto con espacios.", product.getDescription());
        }
    }

    @Nested
    class Rehydrate {

        @Test
        void rehydrate_whenDataIsOk_shouldReturnProduct(){

            Product product = Product.rehydrate(
                    1L,
                    "Cafe",
                    "Producto con data correcta.",
                    new BigDecimal("500.00"),
                    1
            );

            assertEquals(1L, product.getId());
            assertEquals("Cafe", product.getName());
            assertEquals("Producto con data correcta.", product.getDescription());
            assertEquals(0, product.getPrice().compareTo(new BigDecimal("500")));
            assertEquals(1, product.getStock());
        }

        @Test
        void rehydrate_whenIdIsNull_shouldThrowNullPointerException(){

            assertThrows(NullPointerException.class,
                    ()-> Product.rehydrate(
                            null,
                            "Harina",
                            "Producto de prueba.",
                            new BigDecimal("1800.00"),
                            2
                    )
            );
        }

        @Test
        void rehydrate_whenNameIsNullOrBlank_shouldThrowIllegalArgumentException(){

            assertThrows(IllegalArgumentException.class,
                    ()-> Product.rehydrate(1L,
                            null,
                            "Producto de prueba.",
                            new BigDecimal("300"),
                            3)
                );

            assertThrows(IllegalArgumentException.class,
                    ()-> Product.rehydrate(1L,
                            " ",
                            "Producto de prueba.",
                            new BigDecimal("300"),
                            3)
                );
        }

        @Test
        void rehydrate_whenPriceIsNull_shouldThrowNullPointerException(){

            assertThrows(NullPointerException.class,
                    ()-> Product.rehydrate(1L,
                            "Cafe",
                            "Producto de prueba.",
                            null,
                            3)
            );
        }

        @Test
        void rehydrate_whenPriceOrStockAreNegative_shouldThrowIllegalArgumentException(){

            assertThrows(IllegalArgumentException.class,
                    ()-> Product.rehydrate(1L,
                            "Cafe",
                            "Producto de prueba.",
                            new BigDecimal("-300"),
                            3)
            );

            assertThrows(IllegalArgumentException.class,
                    ()-> Product.rehydrate(1L,
                            "Cafe",
                            "Producto de prueba.",
                            new BigDecimal("300"),
                            -3)
            );
        }
    }

    @Nested
    class Immutability{

        @Test
        void withUpdate_whenUpdateProduct_shouldDoesNotChangeOriginal(){

            Product original = Product.of("Galleta",
                    "Producto de prueba.",
                    new BigDecimal("1850.00"),
                    5);

            Product updated = original.withUpdated("Chocolate",
                    "Actualizacion de Producto",
                    new BigDecimal("1580.00"),
                    10);

            assertEquals("Galleta", original.getName());
            assertEquals("Producto de prueba.", original.getDescription());
            assertEquals(0, original.getPrice().compareTo(new BigDecimal("1850.00")));
            assertEquals(5, original.getStock());

            assertEquals("Chocolate", updated.getName());
            assertEquals("Actualizacion de Producto", updated.getDescription());
            assertEquals(0, updated.getPrice().compareTo(new BigDecimal("1580.00")));
            assertEquals(10, updated.getStock());
        }
    }
}