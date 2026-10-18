package com.example.account_service.client;

import com.example.account_service.dto.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CustomerClient {

    private final WebClient customerWebClient;

    public CustomerClient(WebClient customerWebClient){
        this.customerWebClient = customerWebClient;
    }

    public CustomerResponse getCustomer(Long customerId){
        try{
            return customerWebClient
                    .get()
                    .uri("/customers/{id}",customerId)
                    .retrieve()
                    .bodyToMono(CustomerResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer Not Found :"+ customerId);
        }catch(WebClientRequestException exception){
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Customer Service is unavaible");
        }catch(WebClientResponseException ex){
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Customer service returned error");
        }
    }
}
