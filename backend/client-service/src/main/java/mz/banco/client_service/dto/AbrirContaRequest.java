package mz.banco.client_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AbrirContaRequest {

    @NotNull
    private Long clienteId;

    @NotBlank
    @Pattern(regexp = "\\d{4}", message = "O PIN deve ter exactamente 4 digitos")
    private String pin;

    @NotNull
    @DecimalMin(value = "0.00", message = "O saldo inicial nao pode ser negativo")
    private BigDecimal saldoInicial;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }
}