package com.xulambs_aluguel.xulu.service;

import com.xulambs_aluguel.xulu.model.Automovel;
import com.xulambs_aluguel.xulu.model.Empresa;
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

    /**
     * HU da Empresa (Empresa.cadastrarAutomovel no diagrama): a propria
     * empresa autenticada e a proprietaria do automovel cadastrado.
     */
    public Automovel cadastrar(Empresa proprietaria, String placa, int ano, String marca, String modelo) {
        if (automovelRepository.existsByPlaca(placa)) {
            throw new IllegalArgumentException("Ja existe um automovel cadastrado com a placa " + placa);
        }
        return automovelRepository.save(new Automovel(placa, ano, marca, modelo, proprietaria));
    }

    public List<Automovel> listarPorProprietario(Empresa proprietaria) {
        return automovelRepository.findByProprietarioOrderByIdAsc(proprietaria);
    }

    public void marcarIndisponivel(Automovel automovel) {
        automovel.marcarIndisponivel();
        automovelRepository.save(automovel);
    }

    public void marcarDisponivel(Automovel automovel) {
        automovel.marcarDisponivel();
        automovelRepository.save(automovel);
    }
}
