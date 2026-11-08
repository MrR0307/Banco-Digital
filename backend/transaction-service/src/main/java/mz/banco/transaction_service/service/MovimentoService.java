package mz.banco.transaction_service.service;

import mz.banco.transaction_service.dto.DepositoRequest;
import mz.banco.transaction_service.dto.TransferenciaRequest;
import mz.banco.transaction_service.model.Movimento;
import mz.banco.transaction_service.model.TipoTransacao;
import mz.banco.transaction_service.repository.MovimentoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovimentoService {

    private final MovimentoRepository movimentoRepository;
    private final ClientServiceClient clientServiceClient;

    public MovimentoService(
            MovimentoRepository movimentoRepository,
            ClientServiceClient clientServiceClient) {

        this.movimentoRepository = movimentoRepository;
        this.clientServiceClient = clientServiceClient;
    }

    public List<Movimento> listarMovimentos(String numeroConta) {
        return movimentoRepository
                .findByNumeroContaOrderByDataHoraDesc(numeroConta);
    }

    @Transactional
    public Movimento depositar(DepositoRequest request) {

        clientServiceClient.depositar(
                request.getNumeroConta(),
                request.getValor());

        Movimento movimento = new Movimento();
        movimento.setNumeroConta(request.getNumeroConta());
        movimento.setTipo(TipoTransacao.DEPOSITO);
        movimento.setValor(request.getValor());
        movimento.setDescricao(request.getDescricao());

        return movimentoRepository.save(movimento);
    }

    @Transactional
    public Movimento transferir(
            String contaOrigem,
            TransferenciaRequest request) {

        clientServiceClient.transferir(
                contaOrigem,
                request.getContaDestino(),
                request.getValor());

        Movimento saida = new Movimento();
        saida.setNumeroConta(contaOrigem);
        saida.setContaDestino(request.getContaDestino());
        saida.setTipo(TipoTransacao.TRANSFERENCIA);
        saida.setValor(request.getValor());
        saida.setDescricao("Transferência enviada: " + request.getDescricao());

        movimentoRepository.save(saida);

        Movimento entrada = new Movimento();
        entrada.setNumeroConta(request.getContaDestino());
        entrada.setContaDestino(contaOrigem);
        entrada.setTipo(TipoTransacao.TRANSFERENCIA);
        entrada.setValor(request.getValor());
        entrada.setDescricao("Transferência recebida: " + request.getDescricao());

        movimentoRepository.save(entrada);

        return saida;

    }
}