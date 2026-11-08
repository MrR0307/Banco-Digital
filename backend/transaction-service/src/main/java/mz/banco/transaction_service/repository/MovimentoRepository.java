package mz.banco.transaction_service.repository;

import mz.banco.transaction_service.model.Movimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentoRepository extends JpaRepository<Movimento, Long> {

    List<Movimento> findByNumeroContaOrderByDataHoraDesc(String numeroConta);

}