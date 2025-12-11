package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import java.time.LocalDateTime;
import br.com.medflow.dominio.administracao.pacientes.AcaoHistorico;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class PacienteHistoricoEntradaJpa {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "acao_paciente", nullable = false)
    private AcaoHistorico acao;
    
    @Column(name = "descricao_paciente", nullable = false)
    private String descricao;
    
    @Column(name = "responsavel_paciente_id", nullable = false)
    private int responsavelId;
    
    @Column(name = "data_hora_paciente", nullable = false)
    private LocalDateTime dataHora;
    
    // Construtor padrão (obrigatório para JPA)
    protected PacienteHistoricoEntradaJpa() {}
    
    public PacienteHistoricoEntradaJpa(AcaoHistorico acao, String descricao, int responsavelId, LocalDateTime dataHora) {
        this.acao = acao;
        this.descricao = descricao;
        this.responsavelId = responsavelId;
        this.dataHora = dataHora;
    }
    
    // Getters
    public AcaoHistorico getAcao() { return acao; }
    public String getDescricao() { return descricao; }
    public int getResponsavelId() { return responsavelId; }
    public LocalDateTime getDataHora() { return dataHora; }
}