package com.badexample.productservice.infrastructure.web.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id,
                              String name,
                              String description,
                              BigDecimal price,
                              int stock) {}
