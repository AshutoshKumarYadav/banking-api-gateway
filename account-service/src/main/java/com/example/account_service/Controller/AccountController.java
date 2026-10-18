package com.example.account_service.Controller;

import com.example.account_service.Repository.AccountRepository;
import com.example.account_service.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }
    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id){
    Account account = accountRepository.findById(id);
    if(account == null){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Account Not Fount");
    }
    return account;
    }

   /* @GetMapping("/customer/{customerId}")
    public List<Account> getAccountsByCustomer(@PathVariable Long customerId){
        return List.of(
                accountRepository.findById(201L)
        ).stream().filter(account -> account.customerId().equals(customerId)).toList();
    }*/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody Account account){
        return accountRepository.save(account);
    }


    @GetMapping("/customer/{customerId}")
    public List<Account> getAccountsByCustomer(
            @PathVariable Long customerId) {

        return accountRepository.findByCustomerId(customerId);
    }
















}
