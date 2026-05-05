package com.lexiao.assignment2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Csrf {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Correct way to disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Allow all requests without authentication
                )
                .formLogin(form -> form.disable()) // Disable form-based login
                .httpBasic(httpBasic -> httpBasic.disable()); // Disable basic authentication

        return http.build();
    }
}
