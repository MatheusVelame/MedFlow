package br.com.medflow.dominio.referencia.tiposExames;

public interface TipoExameDetalhes {

    Integer getId();
    String getCodigo();
    String getDescricao();
    String getEspecialidade();
    Double getValor();
    StatusTipoExame getStatus();
}
