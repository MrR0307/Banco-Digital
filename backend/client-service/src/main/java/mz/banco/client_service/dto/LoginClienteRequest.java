package mz.banco.client_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginClienteRequest {

    @NotBlank
    private String numeroConta;

    @NotBlank
    @Pattern(regexp = "\\d{4}", message = "O PIN deve ter 4 digitos")
    private String pin;

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}