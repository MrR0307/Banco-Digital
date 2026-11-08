package mz.banco.client_service.controller;

import mz.banco.client_service.dto.LoginClienteRequest;
import mz.banco.client_service.model.Conta;
import mz.banco.client_service.service.ContaService;
import mz.banco.client_service.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clientes/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class ClienteAuthController {

    private final ContaService contaService;
    private final JwtService jwtService;

    public ClienteAuthController(
            ContaService contaService,
            JwtService jwtService) {
        this.contaService = contaService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginClienteRequest request) {

        Conta conta = contaService.autenticar(request);

        String token = jwtService.gerarToken(conta);

        Map<String, Object> resposta = Map.of(
                "tipo", "Bearer",
                "mensagem", "Autenticacao realizada com sucesso!",
                "numeroConta", conta.getNumeroConta(),
                "nome", conta.getCliente().getNome(),
                "saldo", conta.getSaldo(),
                "alterarPin", conta.isAlterarPin(),
                "token", token);

        return ResponseEntity.ok(resposta);
    }
}