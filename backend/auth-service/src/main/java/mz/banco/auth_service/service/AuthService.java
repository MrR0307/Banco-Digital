package mz.banco.auth_service.service;

import mz.banco.auth_service.model.Funcionario;
import mz.banco.auth_service.repository.FuncionarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder) {

        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Funcionario registarFuncionario(
            String nome, String email, String password) {

        if (funcionarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Este email ja esta registado.");
        }

        Funcionario funcionario = new Funcionario();

        funcionario.setNome(nome);
        funcionario.setEmail(email);
        funcionario.setPassword(passwordEncoder.encode(password));
        funcionario.setEstado(true);

        return funcionarioRepository.save(funcionario);
    }

    public Funcionario autenticarFuncionario(
            String email, String password) {

        Funcionario funcionario = funcionarioRepository
                .findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException(
                        "Email ou palavra-passe incorretos."));

        if (!funcionario.getEstado()) {
            throw new DisabledException(
                    "Esta conta esta desativada.");
        }

        if (!passwordEncoder.matches(
                password, funcionario.getPassword())) {

            throw new BadCredentialsException(
                    "Email ou palavra-passe incorretos.");
        }

        return funcionario;
    }
}