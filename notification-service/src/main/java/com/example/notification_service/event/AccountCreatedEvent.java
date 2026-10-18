package com.example.notification_service.event;

import java.time.Instant;

public record AccountCreatedEvent(
        Long accountId,
        Long customerId,
        Instant createdAt
) {
}

