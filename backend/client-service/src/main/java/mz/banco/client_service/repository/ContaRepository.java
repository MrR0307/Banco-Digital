package mz.banco.client_service.repository;

import mz.banco.client_service.model.Conta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumeroConta(String numeroConta);

    List<Conta> findByClienteId(Long clienteId);

    boolean existsByNumeroConta(String numeroConta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Conta c WHERE c.numeroConta = :numeroConta")
    Optional<Conta> encontrarPorNumeroComBloqueio(
            @Param("numeroConta") String numeroConta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Conta> findAllByNumeroContaInOrderByIdAsc(
            List<String> numerosConta);
}