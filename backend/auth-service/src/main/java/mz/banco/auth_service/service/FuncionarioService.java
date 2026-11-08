package mz.banco.auth_service.service;

import mz.banco.auth_service.model.Funcionario;
import mz.banco.auth_service.repository.FuncionarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionario nao encontrado."));
    }

    @Transactional
    public Funcionario atualizarFuncionario(
            Long id, String nome, String email) {

        Funcionario funcionario = buscarPorId(id);

        if (!funcionario.getEmail().equals(email)
                && funcionarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Este email ja esta registado.");
        }

        funcionario.setNome(nome);
        funcionario.setEmail(email);

        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public Funcionario alterarEstado(Long id, boolean estado) {

        Funcionario funcionario = buscarPorId(id);

        funcionario.setEstado(estado);

        return funcionarioRepository.save(funcionario);
    }
}