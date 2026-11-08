package mz.banco.client_service.repository;

import mz.banco.client_service.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDocumento(String documento);

    boolean existsByDocumento(String documento);

    boolean existsByEmail(String email);

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    List<Cliente> findByDocumentoContaining(String documento);

    List<Cliente> findByEmailContainingIgnoreCase(String email);

    List<Cliente> findByNomeContainingIgnoreCaseOrDocumentoContainingOrEmailContainingIgnoreCase(
            String nome, String documento, String email);
}