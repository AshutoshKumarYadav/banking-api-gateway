package com.example.account_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient customerWebClient(WebClient.Builder builder,

                    @Value("${customer.service.url}") String customerServicesUrl){
        return builder.baseUrl(customerServicesUrl).build();

    }
}
