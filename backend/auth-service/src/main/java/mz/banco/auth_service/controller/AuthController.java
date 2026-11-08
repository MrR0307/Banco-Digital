package mz.banco.auth_service.controller;

import mz.banco.auth_service.model.Funcionario;
import mz.banco.auth_service.service.AuthService;
import mz.banco.auth_service.security.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtService jwtService) {

        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> dados) {

        String email = dados.get("email");
        String password = dados.get("password");

        Funcionario funcionario = authService.autenticarFuncionario(email, password);

        String token = jwtService.gerarToken(
                funcionario.getEmail());

        return ResponseEntity.ok(Map.of(
                "mensagem", "Autenticacao realizada com sucesso!",
                "token", token,
                "tipo", "Bearer",
                "id", funcionario.getId(),
                "nome", funcionario.getNome(),
                "email", funcionario.getEmail()));
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> perfil() {
        return ResponseEntity.ok(
                Map.of("mensagem", "JWT funcionando! Acesso autorizado."));
    }
}