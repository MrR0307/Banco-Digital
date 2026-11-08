package mz.banco.client_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AlterarPinRequest {

    @NotBlank
    @Pattern(regexp = "\\d{4}", message = "O PIN actual deve ter 4 digitos")
    private String pinActual;

    @NotBlank
    @Pattern(regexp = "\\d{4}", message = "O novo PIN deve ter 4 digitos")
    private String novoPin;

    public String getPinActual() {
        return pinActual;
    }

    public void setPinActual(String pinActual) {
        this.pinActual = pinActual;
    }

    public String getNovoPin() {
        return novoPin;
    }

    public void setNovoPin(String novoPin) {
        this.novoPin = novoPin;
    }
}