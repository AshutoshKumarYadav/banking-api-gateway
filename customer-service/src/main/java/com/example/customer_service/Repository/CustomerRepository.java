package com.example.customer_service.Repository;

import com.example.customer_service.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CustomerRepository {
    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();

    public CustomerRepository(){
        customers.put(101L,new Customer(101L,"Ashutosh Kumar","ashutosh@gmail.com"));
    }

    public Customer findById(Long id){
        return customers.get(id);
    }

    public Customer save(Customer customer){
        customers.put(customer.id(),customer);
        return customer;
    }
}
