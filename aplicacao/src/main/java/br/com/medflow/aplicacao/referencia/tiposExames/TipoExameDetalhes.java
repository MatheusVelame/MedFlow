package br.com.medflow.aplicacao.referencia.tiposExames;

import java.time.LocalDateTime;
import java.util.List;

import br.com.medflow.dominio.referencia.tiposExames.StatusTipoExame;

public interface TipoExameDetalhes {

    Integer getId();
    String getCodigo();
    String getDescricao();
    String getEspecialidade();
    Double getValor();
    StatusTipoExame getStatus();
}
