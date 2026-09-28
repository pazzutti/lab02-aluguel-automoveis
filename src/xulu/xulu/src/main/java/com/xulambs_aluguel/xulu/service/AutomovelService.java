package com.xulambs_aluguel.xulu.service;

import com.xulambs_aluguel.xulu.model.Automovel;
import com.xulambs_aluguel.xulu.repository.AutomovelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AutomovelService {

    private final AutomovelRepository automovelRepository;

    public AutomovelService(AutomovelRepository automovelRepository) {
        this.automovelRepository = automovelRepository;
    }

    public List<Automovel> listarDisponiveis() {
        return automovelRepository.findByDisponivelTrue();
    }

    public Automovel buscarDisponivelPorId(Long id) {
        Automovel automovel = automovelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Automovel nao encontrado: " + id));
        if (!automovel.isDisponivel()) {
            throw new IllegalStateException("Automovel indisponivel para aluguel");
        }
        return automovel;
    }
}
