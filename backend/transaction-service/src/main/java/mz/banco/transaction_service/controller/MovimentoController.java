package mz.banco.transaction_service.controller;

import mz.banco.transaction_service.dto.DepositoRequest;
import mz.banco.transaction_service.dto.TransferenciaRequest;
import mz.banco.transaction_service.model.Movimento;
import mz.banco.transaction_service.service.MovimentoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class MovimentoController {

    private final MovimentoService movimentoService;

    public MovimentoController(MovimentoService movimentoService) {
        this.movimentoService = movimentoService;
    }

    private boolean isFuncionario(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR")
                || a.getAuthority().equals("ROLE_FUNCIONARIO"));
    }

    @GetMapping("/conta/{numeroConta}")
    public ResponseEntity<List<Movimento>> listarMovimentos(
            @PathVariable String numeroConta,
            Authentication authentication) {

        if (!isFuncionario(authentication)
                && !authentication.getName().equals(numeroConta)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Sem permissao para consultar esta conta");
        }

        return ResponseEntity.ok(
                movimentoService.listarMovimentos(numeroConta));
    }

    @PostMapping("/deposito")
    public ResponseEntity<Movimento> depositar(
            @Valid @RequestBody DepositoRequest request,
            Authentication authentication) {

        if (!isFuncionario(authentication)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Apenas funcionarios podem realizar depositos");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimentoService.depositar(request));
    }

    @PostMapping("/transferencia/{contaOrigem}")
    public ResponseEntity<Movimento> transferir(
            @PathVariable String contaOrigem,
            @Valid @RequestBody TransferenciaRequest request,
            Authentication authentication) {

        if (!isFuncionario(authentication)
                && !authentication.getName().equals(contaOrigem)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Nao pode transferir desta conta");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimentoService.transferir(contaOrigem, request));
    }
}