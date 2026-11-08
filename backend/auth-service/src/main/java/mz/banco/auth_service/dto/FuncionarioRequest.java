package mz.banco.auth_service.dto;

public record FuncionarioRequest(
        String nome,
        String email,
        String password
) {
}
