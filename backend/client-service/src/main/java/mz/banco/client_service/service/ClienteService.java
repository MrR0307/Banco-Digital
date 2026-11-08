package mz.banco.client_service.service;

import mz.banco.client_service.model.Cliente;
import mz.banco.client_service.repository.ClienteRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public Cliente registar(Cliente cliente) {

        if (repository.existsByDocumento(cliente.getDocumento())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Documento ja registado");
        }

        if (cliente.getEmail() != null &&
                repository.existsByEmail(cliente.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email ja registado");
        }

        return repository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
    }

    public Cliente actualizar(Long id, Cliente dados) {

        Cliente cliente = buscarPorId(id);

        if (!cliente.getDocumento().equals(dados.getDocumento())
                && repository.existsByDocumento(dados.getDocumento())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Documento ja utilizado");
        }

        cliente.setNome(dados.getNome());
        cliente.setDocumento(dados.getDocumento());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEmail(dados.getEmail());
        cliente.setEndereco(dados.getEndereco());

        return repository.save(cliente);
    }

    public void eliminar(Long id) {
        Cliente cliente = buscarPorId(id);
        repository.delete(cliente);
    }

    public List<Cliente> pesquisar(String termo) {
        return repository
                .findByNomeContainingIgnoreCaseOrDocumentoContainingOrEmailContainingIgnoreCase(
                        termo, termo, termo);
    }

}