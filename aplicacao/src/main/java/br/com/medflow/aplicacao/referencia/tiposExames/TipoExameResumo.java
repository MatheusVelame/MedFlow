package br.com.medflow.aplicacao.referencia.tiposExames;

import br.com.medflow.dominio.referencia.tiposExames.StatusTipoExame;

public interface TipoExameResumo {
    Integer getId();
    
    String getCodigo();
    
    String getDescricao();
    
    String getEspecialidade();
    
    Double getValor();
    
    StatusTipoExame getStatus();
}