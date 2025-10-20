package com.medflow.dominio.prontuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import java.util.Objects;

/**
 * Representa um registro imutável de histórico clínico de um paciente.
 * Cada registro contém sintomas, diagnóstico, conduta/tratamento e metadados.
 */
public class HistoricoClinico {
    private final String id;
    private final String pacienteId;
    private final String sintomas;
    private final String diagnostico;
    private final String conduta;
    private final LocalDateTime dataHoraRegistro;
    private final String profissionalResponsavel;
    private final List<String> anexosReferenciados;

    public HistoricoClinico(String id, String pacienteId, String sintomas, String diagnostico, String conduta,
                           LocalDateTime dataHoraRegistro, String profissionalResponsavel, List<String> anexosReferenciados) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do histórico é obrigatório.");
        }
        if (pacienteId == null || pacienteId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do paciente é obrigatório.");
        }
        if (sintomas == null || sintomas.trim().isEmpty()) {
            throw new IllegalArgumentException("Sintomas são obrigatórios.");
        }
        if (diagnostico == null || diagnostico.trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnóstico é obrigatório.");
        }
        if (conduta == null || conduta.trim().isEmpty()) {
            throw new IllegalArgumentException("Conduta/tratamento é obrigatório.");
        }
        if (dataHoraRegistro == null) {
            throw new IllegalArgumentException("Data e hora do registro são obrigatórios.");
        }
        if (profissionalResponsavel == null || profissionalResponsavel.trim().isEmpty()) {
            throw new IllegalArgumentException("Profissional responsável é obrigatório.");
        }

        this.id = id;
        this.pacienteId = pacienteId;
        this.sintomas = sintomas;
        this.diagnostico = diagnostico;
        this.conduta = conduta;
        this.dataHoraRegistro = dataHoraRegistro;
        this.profissionalResponsavel = profissionalResponsavel;
        this.anexosReferenciados = anexosReferenciados != null ? Collections.unmodifiableList(anexosReferenciados) : Collections.emptyList();
    }

    // Getters
    public String getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public String getSintomas() { return sintomas; }
    public String getDiagnostico() { return diagnostico; }
    public String getConduta() { return conduta; }
    public LocalDateTime getDataHoraRegistro() { return dataHoraRegistro; }
    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public List<String> getAnexosReferenciados() { return anexosReferenciados; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HistoricoClinico that = (HistoricoClinico) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HistoricoClinico{" +
                "id='" + id + '\'' +
                ", pacienteId='" + pacienteId + '\'' +
                ", dataHoraRegistro=" + dataHoraRegistro +
                ", profissionalResponsavel='" + profissionalResponsavel + '\'' +
                '}';
    }
}
