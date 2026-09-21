package com.xulambs_aluguel.xulu.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Backend e API REST pura (sem paginas server-side): login/logout/erros
 * respondem com JSON simples, sem redirect nem stack trace, para o frontend
 * Vue consumir via fetch. O sistema so pode ser usado apos cadastro previo
 * (HU01/HU03): tudo exige sessao autenticada, exceto o cadastro de usuario.
 *
 * CSRF fica desligado porque nao ha mais formularios Thymeleaf gerando o
 * token automaticamente; a autenticacao continua exigida via cookie de sessao.
 *
 * Os erros aqui escrevem a resposta diretamente (em vez de response.sendError
 * ou deixar a excecao propagar) para nunca passar pelo BasicErrorController
 * padrao do Spring Boot, que inclui a stack trace no corpo JSON.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/clientes", "/api/empresas", "/api/bancos").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .successHandler((request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT))
                        .failureHandler((request, response, exception) ->
                                escreverErro(response, HttpStatus.UNAUTHORIZED, "Login ou senha invalidos"))
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT))
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                escreverErro(response, HttpStatus.UNAUTHORIZED, "Autenticacao necessaria"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                escreverErro(response, HttpStatus.FORBIDDEN, "Acesso negado")));

        return http.build();
    }

    private void escreverErro(HttpServletResponse response, HttpStatus status, String mensagem) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"mensagem\":\"" + mensagem + "\"}");
    }
}
