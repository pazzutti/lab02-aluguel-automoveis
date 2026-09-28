package com.xulambs_aluguel.xulu.repository;

import com.xulambs_aluguel.xulu.model.Automovel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {

    List<Automovel> findByDisponivelTrue();
}
