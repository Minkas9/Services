package com.example.config;

import com.example.Services.enums.Role;
import com.example.Services.model.Customer;
import com.example.Services.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FakeDataLoader {

    private final CustomerRepository customerRepository;

    @PostConstruct
    public void loadData() {
        customerRepository.save(Customer.builder()
                .username("admin")
                .password("admin")
                .customerRole(Role.ROLE_ADMIN)
                .build());
        customerRepository.save(Customer.builder()
                .username("user")
                .password("user")
                .customerRole(Role.ROLE_CUSTOMER)
                .build());
    }

}
