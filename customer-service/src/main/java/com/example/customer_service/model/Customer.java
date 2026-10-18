package com.example.customer_service.model;

public record Customer(
        Long id,
        String name,
        String email
) {}