package mz.banco.transaction_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ClientServiceClient {

    private final RestClient restClient;
    private final String chaveInterna;

    public ClientServiceClient(
            RestClient.Builder builder,
            @Value("${client.service.url}") String url,
            @Value("${internal.api.key}") String chaveInterna) {

        this.restClient = builder.baseUrl(url).build();
        this.chaveInterna = chaveInterna;
    }

    public void depositar(String numeroConta, BigDecimal valor) {

        restClient.post()
                .uri("/api/interno/contas/deposito")
                .header("X-Internal-API-Key", chaveInterna)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "numeroConta", numeroConta,
                        "valor", valor))
                .retrieve()
                .toBodilessEntity();
    }

    public void transferir(
            String contaOrigem,
            String contaDestino,
            BigDecimal valor) {

        restClient.post()
                .uri("/api/interno/contas/transferencia")
                .header("X-Internal-API-Key", chaveInterna)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "contaOrigem", contaOrigem,
                        "contaDestino", contaDestino,
                        "valor", valor))
                .retrieve()
                .toBodilessEntity();
    }
}