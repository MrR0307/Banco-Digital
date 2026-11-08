package mz.banco.auth_service.repository;

import mz.banco.auth_service.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByEmail(String Email);

    boolean existsByEmail(String Email);

}
