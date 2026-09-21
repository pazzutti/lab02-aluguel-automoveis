package com.xulambs_aluguel.xulu.repository;

import com.xulambs_aluguel.xulu.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByLogin(String login);
}
