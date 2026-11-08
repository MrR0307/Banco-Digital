package mz.banco.client_service.controller;

import mz.banco.client_service.dto.DepositoInternoRequest;
import mz.banco.client_service.dto.TransferenciaInternaRequest;
import mz.banco.client_service.service.ContaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/interno/contas")
public class InternalContaController {

    private final ContaService contaService;

    @Value("${internal.api.key}")
    private String chaveInterna;

    public InternalContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    private void validarChave(String chaveRecebida) {
        if (chaveRecebida == null ||
                !MessageDigest.isEqual(
                        chaveInterna.getBytes(StandardCharsets.UTF_8),
                        chaveRecebida.getBytes(StandardCharsets.UTF_8))) {

            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Acesso interno nao autorizado");
        }
    }

    @PostMapping("/deposito")
    public ResponseEntity<Void> depositar(
            @RequestHeader("X-Internal-API-Key") String chave,
            @Valid @RequestBody DepositoInternoRequest request) {

        validarChave(chave);

        contaService.depositar(
                request.getNumeroConta(),
                request.getValor());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transferencia")
    public ResponseEntity<Void> transferir(
            @RequestHeader("X-Internal-API-Key") String chave,
            @Valid @RequestBody TransferenciaInternaRequest request) {

        validarChave(chave);

        contaService.transferir(
                request.getContaOrigem(),
                request.getContaDestino(),
                request.getValor());

        return ResponseEntity.noContent().build();
    }
}