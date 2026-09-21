package com.xulambs_aluguel.xulu.controller;

import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.EntidadeEmpregadora;
import com.xulambs_aluguel.xulu.service.ClienteService;
import com.xulambs_aluguel.xulu.web.ClienteRequest;
import com.xulambs_aluguel.xulu.web.ClienteResponse;
import com.xulambs_aluguel.xulu.web.ClienteUpdateRequest;
import com.xulambs_aluguel.xulu.web.EmpregadoraDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * API REST do cadastro/perfil de cliente, consumida pelo frontend Vue.
 * HU04: um cliente so consulta/altera/exclui os proprios dados -- nao ha
 * endpoint de listagem nem de acesso por id de outro cliente.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    private final ClienteService clienteService;

    public ClienteRestController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse cadastrar(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = new Cliente(request.rg(), request.cpf(), request.nome(), request.endereco(),
                request.profissao(), request.login(), request.senha());
        converterEmpregadoras(request.empregadoras()).forEach(cliente::adicionarEmpregadora);
        return ClienteResponse.from(clienteService.cadastrar(cliente));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ClienteResponse minhaConta(Authentication authentication) {
        return ClienteResponse.from(clienteService.buscarPorLogin(authentication.getName()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ClienteResponse atualizarMinhaConta(Authentication authentication, @Valid @RequestBody ClienteUpdateRequest request) {
        Cliente cliente = clienteService.atualizar(authentication.getName(), request.endereco(), request.profissao(),
                converterEmpregadoras(request.empregadoras()));
        return ClienteResponse.from(cliente);
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirMinhaConta(Authentication authentication) {
        clienteService.excluir(authentication.getName());
    }

    private List<EntidadeEmpregadora> converterEmpregadoras(List<EmpregadoraDTO> empregadoras) {
        if (empregadoras == null) {
            return List.of();
        }
        return empregadoras.stream()
                .filter(e -> e.nome() != null && !e.nome().isBlank() && e.rendimento() != null)
                .map(e -> new EntidadeEmpregadora(e.nome(), e.rendimento()))
                .toList();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> tratarNaoEncontrado(NoSuchElementException e) {
        return Map.of("mensagem", e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarRequisicaoInvalida(RuntimeException e) {
        return Map.of("mensagem", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarValidacao(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Dados invalidos");
        return Map.of("mensagem", mensagem);
    }
}
