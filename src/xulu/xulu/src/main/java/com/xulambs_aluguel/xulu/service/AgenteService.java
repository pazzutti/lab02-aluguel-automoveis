package com.xulambs_aluguel.xulu.service;

import com.xulambs_aluguel.xulu.model.Agente;
import com.xulambs_aluguel.xulu.model.Banco;
import com.xulambs_aluguel.xulu.model.Empresa;
import com.xulambs_aluguel.xulu.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Cadastro dos agentes aderentes (HU01/HU03): empresas e bancos, mais a
 * resolucao do agente autenticado (HU11+) usada ao registrar parecer,
 * cadastrar automovel ou conceder credito.
 */
@Service
public class AgenteService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AgenteService(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    public Empresa cadastrarEmpresa(Empresa empresa) {
        usuarioService.validarECriptografar(empresa);
        return usuarioRepository.save(empresa);
    }

    public Banco cadastrarBanco(Banco banco) {
        usuarioService.validarECriptografar(banco);
        return usuarioRepository.save(banco);
    }

    public Agente buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login)
                .filter(Agente.class::isInstance)
                .map(Agente.class::cast)
                .orElseThrow(() -> new NoSuchElementException("Agente nao encontrado: " + login));
    }

    public Empresa buscarEmpresaPorLogin(String login) {
        Agente agente = buscarPorLogin(login);
        if (!(agente instanceof Empresa empresa)) {
            throw new NoSuchElementException("Empresa nao encontrada: " + login);
        }
        return empresa;
    }

    public Banco buscarBancoPorLogin(String login) {
        Agente agente = buscarPorLogin(login);
        if (!(agente instanceof Banco banco)) {
            throw new NoSuchElementException("Banco nao encontrado: " + login);
        }
        return banco;
    }
}
