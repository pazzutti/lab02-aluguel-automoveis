package com.xulambs_aluguel.xulu.service;

import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.EntidadeEmpregadora;
import com.xulambs_aluguel.xulu.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * HU04: cliente so consulta/altera/exclui os proprios dados -- por isso, a
 * partir da sessao autenticada, toda operacao (exceto o cadastro) e resolvida
 * pelo login de quem esta logado, nunca por um id arbitrario vindo do cliente.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;

    public ClienteService(ClienteRepository clienteRepository, UsuarioService usuarioService) {
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
    }

    public Cliente buscarPorLogin(String login) {
        return clienteRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("Cliente nao encontrado: " + login));
    }

    public Cliente cadastrar(Cliente cliente) {
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new IllegalArgumentException("Ja existe um cliente cadastrado com o CPF " + cliente.getCpf());
        }
        usuarioService.validarECriptografar(cliente);
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(String login, String endereco, String profissao, List<EntidadeEmpregadora> empregadoras) {
        Cliente cliente = buscarPorLogin(login);
        cliente.atualizarDados(endereco, profissao, empregadoras);
        return clienteRepository.save(cliente);
    }

    public void excluir(String login) {
        clienteRepository.delete(buscarPorLogin(login));
    }
}
