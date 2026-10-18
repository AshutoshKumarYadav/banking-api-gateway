package com.example.notification_service.consumer;

import com.example.notification_service.event.AccountCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.stereotype.Component;

@Component
public class AccountCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(AccountCreatedConsumer.class);
    private final LoggingProducerListener<Object, Object> kafkaProducerListener;

    public AccountCreatedConsumer(LoggingProducerListener<Object, Object> kafkaProducerListener) {
        this.kafkaProducerListener = kafkaProducerListener;
    }

    @KafkaListener(topics = "account-created")
    public void consume(AccountCreatedEvent event){
        log.info(
                "NOTIFICATION: Account {} created for customer {} at {}",
                event.accountId(),
                event.customerId(),
                event.createdAt()
        );
    }
}
