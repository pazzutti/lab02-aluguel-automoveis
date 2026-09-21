package com.xulambs_aluguel.xulu.config;

import com.xulambs_aluguel.xulu.model.Banco;
import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.Empresa;
import com.xulambs_aluguel.xulu.repository.UsuarioRepository;
import com.xulambs_aluguel.xulu.service.AgenteService;
import com.xulambs_aluguel.xulu.service.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Cria uma conta de exemplo para cada tipo de usuario (Cliente/Empresa/Banco)
 * na subida da aplicacao, so para facilitar testes manuais e demos.
 * Login = nome do tipo, senha = "Senha123" para todas -- nao usar em producao.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String SENHA_PADRAO = "Senha123";

    private final UsuarioRepository usuarioRepository;
    private final ClienteService clienteService;
    private final AgenteService agenteService;

    public DataSeeder(UsuarioRepository usuarioRepository, ClienteService clienteService,
                       AgenteService agenteService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteService = clienteService;
        this.agenteService = agenteService;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByLogin("Cliente")) {
            clienteService.cadastrar(new Cliente("123456789", "12345678900", "Cliente Exemplo",
                    "Rua Exemplo, 100", "Autonomo", "Cliente", SENHA_PADRAO));
            log.info("Conta de exemplo criada: login=Cliente senha={}", SENHA_PADRAO);
        }
        if (!usuarioRepository.existsByLogin("Empresa")) {
            agenteService.cadastrarEmpresa(new Empresa("Empresa Exemplo Ltda", "Empresa", SENHA_PADRAO));
            log.info("Conta de exemplo criada: login=Empresa senha={}", SENHA_PADRAO);
        }
        if (!usuarioRepository.existsByLogin("Banco")) {
            agenteService.cadastrarBanco(new Banco("Banco Exemplo S.A.", "Banco", SENHA_PADRAO));
            log.info("Conta de exemplo criada: login=Banco senha={}", SENHA_PADRAO);
        }
    }
}
