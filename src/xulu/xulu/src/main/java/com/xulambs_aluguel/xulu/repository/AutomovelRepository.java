package com.xulambs_aluguel.xulu.repository;

import com.xulambs_aluguel.xulu.model.Automovel;
import com.xulambs_aluguel.xulu.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {

    List<Automovel> findByDisponivelTrue();

    boolean existsByPlaca(String placa);

    List<Automovel> findByProprietarioOrderByIdAsc(Usuario proprietario);
}
