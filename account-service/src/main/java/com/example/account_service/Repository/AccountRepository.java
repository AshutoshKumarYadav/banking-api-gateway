package com.example.account_service.Repository;

import com.example.account_service.model.Account;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();

    public AccountRepository(){
        accounts.put(201L,new Account(201L,101L,"accountNumber",new BigDecimal(5000000)));

    }

    public Account findById(Long id){
        return accounts.get(id);
    }

    public Account save(Account account){
        accounts.put(account.id(),account);
        return account;
    }

    public List<Account> findByCustomerId(Long customerId) {
        return accounts.values()
                .stream()
                .filter(account ->
                        account.customerId().equals(customerId))
                .toList();
    }


}
