package com.example.api_gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class SecurityConfig {
    @Bean
    public SecretKey jwtSecretKey(
            @Value("${security.jwt.secret}") String secret
    ){
        byte[] decoder = Base64.getDecoder().decode(secret);

        if(decoder.length<32){
        throw new IllegalArgumentException("JWT secret must contain at least 32 bytes");
        }
        return new SecretKeySpec(decoder,"HmacSHA256");
    }
    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(
                new com.nimbusds.jose.jwk.source.ImmutableSecret<>(
                        secretKey
                )
        );
    }
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(SecretKey jwtSecretKey) {
        NimbusReactiveJwtDecoder decoder =
                NimbusReactiveJwtDecoder.withSecretKey(jwtSecretKey)
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();

        return decoder;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public ReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder){
        UserDetails user = User.withUsername("ashutosh")
                .password(passwordEncoder.encode("Banking@123")).roles("USER").build();

        UserDetails admin = User.withUsername("admin").password(passwordEncoder.encode("Admin@123")).roles("ADMIN").build();

        return username -> {
            if("ashutosh".equals(username)){
                return Mono.just(user);
            }

            if("admin".equals(username)){
                return Mono.just(admin);
            }

            return Mono.empty();
        };
    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        ReactiveJwtAuthenticationConverterAdapter reactiveJwtAuthenticationConverter = new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(
            exchange-> exchange
        // Login is public
        .pathMatchers("/auth/login").permitAll()
        // Account creation is admin-only
                    .pathMatchers(HttpMethod.POST, "/api/accounts")
                    .hasRole("ADMIN")
        // All other customer and account requests require login
        .pathMatchers(
                "/api/customers/**",
                "/api/accounts/**"
        ).authenticated()
                        // Other routes require authentication as well
        .anyExchange().authenticated()

                ).oauth2ResourceServer(
                        oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(
                                jwt->jwt.jwtAuthenticationConverter(reactiveJwtAuthenticationConverter)
                        )
                ).build();
    }


    @Bean
    public org.springframework.security.authentication.ReactiveAuthenticationManager
    authenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        var manager =
                new org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager(
                        userDetailsService
                );

        manager.setPasswordEncoder(passwordEncoder);

        return manager;
    }


































}
