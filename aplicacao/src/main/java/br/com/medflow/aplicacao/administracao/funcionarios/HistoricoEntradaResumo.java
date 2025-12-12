package br.com.medflow.aplicacao.administracao.funcionarios;

import java.time.LocalDateTime;

public interface HistoricoEntradaResumo {
    String getAcao(); 
    String getDescricao();
    Integer getResponsavelId();
    LocalDateTime getDataHora();
}
