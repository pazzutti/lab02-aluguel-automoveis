package com.xulambs_aluguel.xulu.config;

import com.xulambs_aluguel.xulu.model.Automovel;
import com.xulambs_aluguel.xulu.model.Banco;
import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.Empresa;
import com.xulambs_aluguel.xulu.repository.AutomovelRepository;
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
    private final AutomovelRepository automovelRepository;

    public DataSeeder(UsuarioRepository usuarioRepository, ClienteService clienteService,
                       AgenteService agenteService, AutomovelRepository automovelRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteService = clienteService;
        this.agenteService = agenteService;
        this.automovelRepository = automovelRepository;
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
        seedAutomoveis();
    }

    /**
     * Cadastro de automovel (Empresa.cadastrarAutomovel) ainda nao tem
     * endpoint proprio, entao a frota de demonstracao vem daqui, pertencendo
     * a Empresa Exemplo.
     */
    private void seedAutomoveis() {
        if (automovelRepository.count() > 0) {
            return;
        }
        Empresa empresaExemplo = (Empresa) usuarioRepository.findByLogin("Empresa")
                .orElseThrow(() -> new IllegalStateException("Empresa de exemplo nao encontrada para seed de automoveis"));

        automovelRepository.save(new Automovel("ABC1D23", 2022, "Fiat", "Argo", empresaExemplo));
        automovelRepository.save(new Automovel("DEF4E56", 2023, "Chevrolet", "Onix", empresaExemplo));
        automovelRepository.save(new Automovel("GHI7F89", 2021, "Toyota", "Corolla", empresaExemplo));
        log.info("Automoveis de exemplo cadastrados, propriedade de {}", empresaExemplo.getNomeInstituicao());
    }
}
