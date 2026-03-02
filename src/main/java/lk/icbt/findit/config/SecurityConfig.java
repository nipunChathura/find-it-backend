package lk.icbt.findit.config;

import lk.icbt.findit.security.CustomUserDetailsService;
import lk.icbt.findit.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;
    private final ApiRequestLoggingFilter apiRequestLoggingFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/registration").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/merchants/onboarding").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/merchants/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customers/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customers/onboarding").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/merchants/profile").hasRole("MERCHANT")
                        .requestMatchers(HttpMethod.POST, "/api/sub-merchants").hasAnyRole("SYSADMIN", "ADMIN", "MERCHANT")
                        .requestMatchers("/api/admin/**").hasAnyRole("SYSADMIN", "ADMIN")
                        .requestMatchers("/api/customers/**").hasAnyRole("SYSADMIN", "ADMIN")
                        .requestMatchers("/api/categories/**").hasAnyRole("SYSADMIN", "ADMIN", "MERCHANT", "SUBMERCHANT")
                        .requestMatchers("/api/provinces/**").hasAnyRole("SYSADMIN", "ADMIN", "MERCHANT", "SUBMERCHANT")
                        .requestMatchers("/api/districts/**").hasAnyRole("SYSADMIN", "ADMIN", "MERCHANT", "SUBMERCHANT")
                        .requestMatchers("/api/outlets/**").hasAnyRole("SYSADMIN", "ADMIN", "MERCHANT", "SUBMERCHANT")
                        .requestMatchers("/api/items/**").hasAnyRole("SYSADMIN", "ADMIN", "MERCHANT", "SUBMERCHANT")
                        .requestMatchers("/api/discounts/**").hasAnyRole("SYSADMIN", "ADMIN", "MERCHANT", "SUBMERCHANT")
                        .requestMatchers("/api/payments/**").hasAnyRole("SYSADMIN", "ADMIN", "MERCHANT", "SUBMERCHANT")
                        .requestMatchers("/api/notifications/**").hasAnyRole("SYSADMIN", "ADMIN", "MERCHANT", "SUBMERCHANT", "USER", "CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/password/change").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/password/forgot").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/password/forgot/approval/**").hasRole("SYSADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {})
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiRequestLoggingFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
