package mz.banco.auth_service.dto;

public record FuncionarioResponse(
        Long id,
        String nome,
        String email,
        boolean estado
) {
}