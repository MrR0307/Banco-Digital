package mz.banco.client_service.config;

import mz.banco.client_service.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/api/clientes/auth/login").permitAll()

                        // A chave interna será validada no InternalContaController
                        .requestMatchers(HttpMethod.POST,
                                "/api/interno/contas/deposito",
                                "/api/interno/contas/transferencia")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/contas/minha-conta/saldo")
                        .hasRole("CLIENTE")

                        .requestMatchers(HttpMethod.PATCH, "/api/contas/*/pin")
                        .hasAnyRole("ADMINISTRADOR", "FUNCIONARIO", "CLIENTE")

                        .requestMatchers("/api/clientes/**")
                        .hasAnyRole("ADMINISTRADOR", "FUNCIONARIO")

                        .requestMatchers("/api/contas/**")
                        .hasAnyRole("ADMINISTRADOR", "FUNCIONARIO")

                        .anyRequest().authenticated())
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}