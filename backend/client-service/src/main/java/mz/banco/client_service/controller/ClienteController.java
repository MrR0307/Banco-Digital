package mz.banco.client_service.controller;

import mz.banco.client_service.model.Cliente;
import mz.banco.client_service.service.ClienteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Cliente> registar(@RequestBody Cliente cliente) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registar(cliente));
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(
            @PathVariable Long id,
            @RequestBody Cliente cliente) {
        return ResponseEntity.ok(service.actualizar(id, cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<Cliente>> pesquisar(
            @RequestParam String termo) {

        return ResponseEntity.ok(service.pesquisar(termo));
    }
    
}