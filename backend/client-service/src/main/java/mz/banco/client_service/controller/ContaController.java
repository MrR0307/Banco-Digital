package mz.banco.client_service.controller;

import mz.banco.client_service.dto.AbrirContaRequest;
import mz.banco.client_service.model.Conta;
import mz.banco.client_service.service.ContaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import mz.banco.client_service.dto.AlterarPinRequest;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/contas")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Conta> abrirConta(
            @Valid @RequestBody AbrirContaRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.abrirConta(request));
    }

    @GetMapping
    public ResponseEntity<List<Conta>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Conta>> listarPorCliente(
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Conta> alterarEstado(
            @PathVariable Long id,
            @RequestParam boolean estado) {

        return ResponseEntity.ok(service.alterarEstado(id, estado));
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<Void> alterarPin(
            @PathVariable Long id,
            @Valid @RequestBody AlterarPinRequest request) {

        service.alterarPin(id, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/minha-conta/saldo")
    public ResponseEntity<Map<String, BigDecimal>> consultarMeuSaldo(
            Authentication authentication) {

        String numeroConta = authentication.getName();

        BigDecimal saldo = service.consultarSaldoPorNumeroConta(numeroConta);

        return ResponseEntity.ok(Map.of("saldo", saldo));
    }
}