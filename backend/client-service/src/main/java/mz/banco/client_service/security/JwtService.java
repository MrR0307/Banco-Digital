package mz.banco.client_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import mz.banco.client_service.model.Conta;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public String extrairRole(String token) {
        return extrairClaims(token).get("role", String.class);
    }

    public boolean validarToken(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String gerarToken(Conta conta) {

        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiration);

        return Jwts.builder()
                .subject(conta.getNumeroConta())
                .claim("role", "CLIENTE")
                .claim("contaId", conta.getId())
                .claim("alterarPin", conta.isAlterarPin())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getSigningKey())
                .compact();
    }

    public Long extrairContaId(String token) {
        return extrairClaims(token).get("contaId", Long.class);
    }

    public boolean extrairAlterarPin(String token) {
        return Boolean.TRUE.equals(
                extrairClaims(token).get("alterarPin", Boolean.class));
    }
}