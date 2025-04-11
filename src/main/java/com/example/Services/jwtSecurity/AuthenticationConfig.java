package com.example.Services.jwtSecurity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

/**
 * Ši klasė yra Spring konfigūracija, kuri nustato autentifikavimui reikalingus komponentus.
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    // Paslaugų klasė, kuri įkelia vartotojo duomenis pagal naudotojo vardą
    private final UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig,
            UserDetailsService userDetailsService,
            BCryptPasswordEncoder passwordEncoder) throws Exception {

        // Sukuriamas autentifikavimo tiekėjas, kuris naudoja mūsų UserDetailsService ir šifravimą
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        // Sukuriamas ir grąžinamas autentifikavimo valdytojas (manager)
        return new ProviderManager(Collections.singletonList(provider));
    }
}