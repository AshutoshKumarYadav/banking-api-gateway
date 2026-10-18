package com.example.account_service.model;

import java.math.BigDecimal;

public record Account(
    Long id,
    Long customerId,
    String accountNumber,
    BigDecimal balance
){}
