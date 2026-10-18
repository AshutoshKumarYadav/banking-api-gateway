
package com.example.account_service.Repository;

import com.example.account_service.model.Account;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class AccountRepository {

    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();

    // Generates unique account IDs.
    private final AtomicLong idGenerator = new AtomicLong(201L);

    public AccountRepository() {
        accounts.put(
                201L,
                new Account(
                        201L,
                        101L,
                        "accountNumber",
                        BigDecimal.valueOf(5000000)
                )
        );
    }

    public Account findById(Long id) {
        return accounts.get(id);
    }

    public Account save(Account account) {

        // Generate an ID if the client did not provide one.
        Long accountId;

        if (account.id() == null) {
            accountId = idGenerator.incrementAndGet();
        } else {
            accountId = account.id();

            // Prevent future generated IDs from colliding with this ID.
            idGenerator.updateAndGet(
                    currentId -> Math.max(currentId, accountId)
            );
        }

        // Account is a record, so create a new instance with the generated ID.
        Account savedAccount = new Account(
                accountId,
                account.customerId(),
                account.accountNumber(),
                account.balance()
        );

        accounts.put(accountId, savedAccount);

        return savedAccount;
    }

    public List<Account> findByCustomerId(Long customerId) {
        return accounts.values()
                .stream()
                .filter(account ->
                        account.customerId() != null
                                && account.customerId().equals(customerId))
                .toList();
    }
}
