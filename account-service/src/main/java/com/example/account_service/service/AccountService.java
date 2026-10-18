package com.example.account_service.service;

import com.example.account_service.Repository.AccountRepository;
import com.example.account_service.client.CustomerClient;
import com.example.account_service.event.AccountCreatedEvent;
import com.example.account_service.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AccountService {


    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private static final String ACCOUNT_CREATED_TOPIC = "account-created";


    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;
    private final KafkaTemplate<String, AccountCreatedEvent> kafkaTemplate;

    public AccountService(AccountRepository accountRepository, CustomerClient customerClient,KafkaTemplate<String,AccountCreatedEvent> kafkaTemplate){
        this.accountRepository = accountRepository;
        this.customerClient = customerClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Account getAccount(Long id){
        return accountRepository.findById(id);
    }

    public List<Account> getAccountByCustomer(Long customerId){
        return accountRepository.findByCustomerId(customerId);
    }

    public Account createAccount(Account account){
        // Step 1: Verify that the customer exists.
        customerClient.getCustomer(account.customerId());

        // Step 2: Save only after successful customer verification.
        Account savedAccount =  accountRepository.save(account);

        // Step 3: Create the event.
        AccountCreatedEvent event = new AccountCreatedEvent(
                savedAccount.id(),
        savedAccount.customerId(),
        Instant.now()
        );

        // Step 4: Publish the event to Kafka.
            kafkaTemplate.send(
                    ACCOUNT_CREATED_TOPIC,
                    String.valueOf(savedAccount.id()),
                    event
            ).whenComplete((result,exception)-> {
                if (exception != null) {
                    log.error("Failed to publish AccountCreatedEvent for account {}", savedAccount.id(), exception
                    );
                } else {
                    log.info("AccountCreatedEvent published. Account ID: {}, partition: {}", savedAccount.id(), result.getRecordMetadata().partition()

                    );
                }
            });
            return savedAccount;
    }

}
