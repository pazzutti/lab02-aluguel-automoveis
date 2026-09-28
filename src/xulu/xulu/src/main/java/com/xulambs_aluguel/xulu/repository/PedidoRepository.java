package com.xulambs_aluguel.xulu.repository;

import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.Pedido;
import com.xulambs_aluguel.xulu.model.SituacaoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteOrderByDataCriacaoDesc(Cliente cliente);

    List<Pedido> findBySituacaoInOrderByDataCriacaoAsc(Collection<SituacaoPedido> situacoes);
}
