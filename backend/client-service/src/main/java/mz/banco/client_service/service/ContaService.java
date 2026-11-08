package mz.banco.client_service.service;

import mz.banco.client_service.dto.AbrirContaRequest;
import mz.banco.client_service.model.Cliente;
import mz.banco.client_service.model.Conta;
import mz.banco.client_service.repository.ClienteRepository;
import mz.banco.client_service.repository.ContaRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.math.BigDecimal;

import mz.banco.client_service.dto.AlterarPinRequest;
import mz.banco.client_service.dto.LoginClienteRequest;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom random = new SecureRandom();

    public ContaService(
            ContaRepository contaRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder) {

        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Conta abrirConta(AbrirContaRequest request) {

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente nao encontrado"));

        if (request.getSaldoInicial().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Saldo inicial invalido");
        }

        Conta conta = new Conta();

        conta.setCliente(cliente);
        conta.setNumeroConta(gerarNumeroConta());
        conta.setPin(passwordEncoder.encode(request.getPin()));
        conta.setSaldo(request.getSaldoInicial());
        conta.setEstado(true);
        conta.setAlterarPin(true);

        return contaRepository.save(conta);
    }

    private String gerarNumeroConta() {

        String numero;

        do {
            numero = String.format("%010d",
                    random.nextInt(1_000_000_000));
        } while (contaRepository.existsByNumeroConta(numero));

        return numero;
    }

    public java.util.List<Conta> listarTodas() {
        return contaRepository.findAll();
    }

    public java.util.List<Conta> listarPorCliente(Long clienteId) {

        if (!clienteRepository.existsById(clienteId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Cliente nao encontrado");
        }

        return contaRepository.findByClienteId(clienteId);
    }

    public Conta buscarPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conta nao encontrada"));
    }

    public Conta alterarEstado(Long id, boolean estado) {

        Conta conta = buscarPorId(id);

        conta.setEstado(estado);

        return contaRepository.save(conta);
    }

    @Transactional
    public void alterarPin(Long contaId, AlterarPinRequest request) {

        Conta conta = buscarPorId(contaId);

        if (!passwordEncoder.matches(request.getPinActual(), conta.getPin())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "PIN actual incorrecto");
        }

        if (request.getPinActual().equals(request.getNovoPin())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O novo PIN deve ser diferente");
        }

        conta.setPin(passwordEncoder.encode(request.getNovoPin()));
        conta.setAlterarPin(false);

        contaRepository.save(conta);
    }

    public Conta autenticar(LoginClienteRequest request) {

        Conta conta = contaRepository.findByNumeroConta(request.getNumeroConta())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Numero de conta ou PIN incorrecto"));

        if (!conta.isEstado()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Esta conta encontra-se bloqueada");
        }

        if (!passwordEncoder.matches(request.getPin(), conta.getPin())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Numero de conta ou PIN incorrecto");
        }

        return conta;
    }

    public BigDecimal consultarSaldoPorNumeroConta(String numeroConta) {

        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conta nao encontrada"));

        if (!conta.isEstado()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta conta encontra-se bloqueada");
        }

        return conta.getSaldo();
    }

    @Transactional
    public void depositar(String numeroConta, BigDecimal valor) {

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Valor de deposito invalido");
        }

        Conta conta = contaRepository
                .encontrarPorNumeroComBloqueio(numeroConta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conta nao encontrada"));

        if (!conta.isEstado()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Conta bloqueada");
        }

        conta.setSaldo(conta.getSaldo().add(valor));

        contaRepository.save(conta);
    }

    @Transactional
    public void transferir(
            String origem,
            String destino,
            BigDecimal valor) {

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Valor de transferencia invalido");
        }

        if (origem.equals(destino)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "As contas devem ser diferentes");
        }

        java.util.List<Conta> contas = contaRepository.findAllByNumeroContaInOrderByIdAsc(
                java.util.List.of(origem, destino));

        if (contas.size() != 2) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Uma das contas nao foi encontrada");
        }

        Conta contaOrigem = contas.stream()
                .filter(c -> c.getNumeroConta().equals(origem))
                .findFirst().orElseThrow();

        Conta contaDestino = contas.stream()
                .filter(c -> c.getNumeroConta().equals(destino))
                .findFirst().orElseThrow();

        if (!contaOrigem.isEstado() || !contaDestino.isEstado()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Uma das contas esta bloqueada");
        }

        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));

        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

        contaRepository.saveAll(contas);
    }
}