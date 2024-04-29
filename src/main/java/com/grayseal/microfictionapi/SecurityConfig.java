package com.grayseal.microfictionapi;

import com.grayseal.microfictionapi.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security setup.
 */
@Configuration
public class SecurityConfig {

    /**
     * Defines a bean for PasswordEncoder to use BCrypt hashing algorithm.
     *
     * @return An instance of BCryptPasswordEncoder.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures security filter chain.
     *
     * @param http HttpSecurity object for configuring security settings.
     * @return Configured SecurityFilterChain.
     * @throws Exception If an error occurs while configuring security.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((authorize) -> {
                    authorize.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults()) // Configures HTTP Basic authentication with default settings
                .csrf(csrf -> csrf.disable()) // Disables CSRF protection
                .build(); // Return the configured HttpSecurity object
    }

    /**
     * Defines a bean for UserDetailsService to load user-specific data.
     *
     * @return An instance of UserDetailsServiceImpl.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    /**
     * Defines a bean for AuthenticationProvider to authenticate users.
     *
     * @return An instance of DaoAuthenticationProvider configured with UserDetailsServiceImpl and PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }
}