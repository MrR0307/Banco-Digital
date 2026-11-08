package mz.banco.auth_service.service;

import mz.banco.auth_service.model.Funcionario;
import mz.banco.auth_service.model.Role;
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

                Funcionario admin = funcionarioRepository
                        .findByEmail(email)
                        .orElseThrow();

                admin.setRole(Role.ADMINISTRADOR);

                funcionarioRepository.save(admin);

                System.out.println("Administrador criado com sucesso!");

            } else {

                Funcionario admin = funcionarioRepository
                        .findByEmail(email)
                        .orElseThrow();

                admin.setRole(Role.ADMINISTRADOR);

                funcionarioRepository.save(admin);

                System.out.println("Administrador ja existe.");
            }
        };
    }
}