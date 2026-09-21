package com.xulambs_aluguel.xulu.service;

import com.xulambs_aluguel.xulu.model.Usuario;
import com.xulambs_aluguel.xulu.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Validacoes e criptografia de senha comuns a todo cadastro de usuario
 * (Cliente, Empresa, Banco) -- HU01/HU03.
 */
@Service
public class UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validarECriptografar(Usuario usuario) {
        if (usuario.getLogin() == null || usuario.getLogin().isBlank()) {
            throw new IllegalArgumentException("Login e obrigatorio");
        }
        if (usuarioRepository.existsByLogin(usuario.getLogin())) {
            throw new IllegalArgumentException("Ja existe um usuario cadastrado com o login " + usuario.getLogin());
        }
        if (usuario.getSenha() == null || usuario.getSenha().length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException("Senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres");
        }
        usuario.criptografarSenha(passwordEncoder.encode(usuario.getSenha()));
    }
}
