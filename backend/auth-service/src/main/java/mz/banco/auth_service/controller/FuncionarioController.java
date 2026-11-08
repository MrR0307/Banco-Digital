package mz.banco.auth_service.controller;

import mz.banco.auth_service.dto.FuncionarioRequest;
import mz.banco.auth_service.dto.FuncionarioResponse;
import mz.banco.auth_service.model.Funcionario;
import mz.banco.auth_service.service.AuthService;
import mz.banco.auth_service.service.FuncionarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final AuthService authService;
    private final FuncionarioService funcionarioService;

    public FuncionarioController(
            AuthService authService,
            FuncionarioService funcionarioService) {

        this.authService = authService;
        this.funcionarioService = funcionarioService;
    }

    private FuncionarioResponse converterResposta(Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getEmail(),
                funcionario.getEstado());
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponse>> listar() {

        List<FuncionarioResponse> funcionarios = funcionarioService.listarFuncionarios()
                .stream()
                .map(this::converterResposta)
                .toList();

        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> buscar(@PathVariable Long id) {

        Funcionario funcionario = funcionarioService.buscarPorId(id);

        return ResponseEntity.ok(converterResposta(funcionario));
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponse> registar(
            @RequestBody FuncionarioRequest dados) {

        Funcionario funcionario = authService.registarFuncionario(
                dados.nome(),
                dados.email(),
                dados.password());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(converterResposta(funcionario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> atualizar(
            @PathVariable Long id,
            @RequestBody FuncionarioRequest dados) {

        Funcionario funcionario = funcionarioService.atualizarFuncionario(
                id,
                dados.nome(),
                dados.email());

        return ResponseEntity.ok(converterResposta(funcionario));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<FuncionarioResponse> alterarEstado(
            @PathVariable Long id,
            @RequestParam boolean estado) {

        Funcionario funcionario = funcionarioService.alterarEstado(id, estado);

        return ResponseEntity.ok(converterResposta(funcionario));
    }
}