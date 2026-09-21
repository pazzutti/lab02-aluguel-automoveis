package com.xulambs_aluguel.xulu.security;

import com.xulambs_aluguel.xulu.model.Banco;
import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.Empresa;
import com.xulambs_aluguel.xulu.model.Usuario;
import com.xulambs_aluguel.xulu.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Autentica qualquer Usuario (Cliente, Empresa ou Banco) pelo login/senha
 * cadastrados (HU01/HU03). O papel (role) e derivado do tipo concreto, para
 * dar suporte a autorizacao por tipo de usuario mais adiante.
 */
@Service
public class UsuarioUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + login));

        return User.builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .roles(papel(usuario))
                .build();
    }

    private String papel(Usuario usuario) {
        if (usuario instanceof Cliente) {
            return "CLIENTE";
        }
        if (usuario instanceof Empresa) {
            return "EMPRESA";
        }
        if (usuario instanceof Banco) {
            return "BANCO";
        }
        return "AGENTE";
    }
}
