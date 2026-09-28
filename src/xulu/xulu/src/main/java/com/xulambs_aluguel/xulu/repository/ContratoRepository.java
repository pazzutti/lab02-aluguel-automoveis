package com.xulambs_aluguel.xulu.repository;

import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.Contrato;
import com.xulambs_aluguel.xulu.model.Leasing;
import com.xulambs_aluguel.xulu.model.Locacao;
import com.xulambs_aluguel.xulu.model.SituacaoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByClienteOrderByDataInicioDesc(Cliente cliente);

    @Query("select l from Locacao l where l.situacao = :situacao order by l.dataInicio asc")
    List<Locacao> buscarLocacoesPorSituacao(@Param("situacao") SituacaoContrato situacao);

    @Query("select l from Leasing l where l.situacao = :situacao order by l.dataInicio asc")
    List<Leasing> buscarLeasingsPorSituacao(@Param("situacao") SituacaoContrato situacao);
}
