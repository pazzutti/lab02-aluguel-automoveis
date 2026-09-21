package com.xulambs_aluguel.xulu.service;

import com.xulambs_aluguel.xulu.model.Banco;
import com.xulambs_aluguel.xulu.model.Empresa;
import com.xulambs_aluguel.xulu.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

/**
 * Cadastro dos agentes aderentes (HU01/HU03): empresas e bancos. Empresa e
 * Banco ainda nao tem comportamento proprio (HU11+), entao por enquanto isto
 * e so o cadastro/login.
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
}
