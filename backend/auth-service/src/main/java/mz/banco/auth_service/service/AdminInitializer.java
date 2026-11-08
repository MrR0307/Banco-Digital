package mz.banco.auth_service.service;

import mz.banco.auth_service.repository.FuncionarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner criarAdmin(
            AuthService authService,
            FuncionarioRepository funcionarioRepository) {

        return args -> {

            String email = "admin@banco.co.mz";

            if (!funcionarioRepository.existsByEmail(email)) {

                authService.registarFuncionario(
                        "Administrador",
                        email,
                        "Admin123!");

                System.out.println("Administrador criado com sucesso!");
            } else {
                System.out.println("Administrador ja existe.");
            }
        };
    }
}