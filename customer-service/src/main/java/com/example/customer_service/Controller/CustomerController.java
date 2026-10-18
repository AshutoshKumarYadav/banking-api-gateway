package com.example.customer_service.Controller;

import com.example.customer_service.Repository.CustomerRepository;
import com.example.customer_service.model.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository){
        this.repository = repository;
    }
    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id){
        Customer customer = repository.findById(id);
        if(customer == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,"Customer not found"
            );
        }
        return customer;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestBody Customer customer){
        return repository.save(customer);
    }

}
