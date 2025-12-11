package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.medflow.dominio.administracao.pacientes.Paciente;
import br.com.medflow.dominio.administracao.pacientes.Paciente.HistoricoEntrada;
import br.com.medflow.dominio.administracao.pacientes.PacienteId;
import br.com.medflow.dominio.administracao.pacientes.UsuarioResponsavelId;

public class PacienteJpaMapeador {
    
    /**
     * Converte de Domínio (Paciente) para JPA (PacienteJpa)
     */
    public static PacienteJpa paraJpa(Paciente paciente) {
        if (paciente == null) {
            return null;
        }
        
        PacienteJpa jpa = new PacienteJpa(
            paciente.getNome(),
            paciente.getCpf(),
            paciente.getDataNascimento(),
            paciente.getTelefone(),
            paciente.getEndereco()
        );
        
        // Se o paciente já tem ID (atualização), setar o ID
        if (paciente.getId() != null) {
            jpa.setId(paciente.getId().getId());
        }
        
        // Converter histórico
        List<PacienteHistoricoEntradaJpa> historicoJpa = paciente.getHistorico().stream()
            .map(PacienteJpaMapeador::paraHistoricoJpa)
            .collect(Collectors.toList());
        
        jpa.setHistorico(historicoJpa);
        
        return jpa;
    }
    
    /**
     * Converte de JPA (PacienteJpa) para Domínio (Paciente)
     */
    public static Paciente paraDominio(PacienteJpa jpa) {
        if (jpa == null) {
            return null;
        }
        
        PacienteId id = new PacienteId(jpa.getId());
        
        // Converter histórico
        List<HistoricoEntrada> historicoDominio = jpa.getHistorico().stream()
            .map(PacienteJpaMapeador::paraHistoricoDominio)
            .collect(Collectors.toList());
        
        // Usar construtor de reconstrução
        return new Paciente(
            id,
            jpa.getNome(),
            jpa.getCpf(),
            jpa.getDataNascimento(),
            jpa.getTelefone(),
            jpa.getEndereco(),
            historicoDominio
        );
    }
    
    /**
     * Converte histórico de Domínio para JPA
     */
    private static PacienteHistoricoEntradaJpa paraHistoricoJpa(HistoricoEntrada entrada) {
        return new PacienteHistoricoEntradaJpa(
            entrada.getAcao(),
            entrada.getDescricao(),
            entrada.getResponsavel().getId(),
            entrada.getDataHora()
        );
    }
    
    /**
     * Converte histórico de JPA para Domínio
     */
    private static HistoricoEntrada paraHistoricoDominio(PacienteHistoricoEntradaJpa jpa) {
        UsuarioResponsavelId responsavel = new UsuarioResponsavelId(jpa.getResponsavelId());
        
        return new HistoricoEntrada(
            jpa.getAcao(),
            jpa.getDescricao(),
            responsavel,
            jpa.getDataHora()
        );
    }
    
    /**
     * Converte uma lista de JPA para Domínio
     */
    public static List<Paciente> paraDominioLista(List<PacienteJpa> listaJpa) {
        if (listaJpa == null) {
            return new ArrayList<>();
        }
        
        return listaJpa.stream()
            .map(PacienteJpaMapeador::paraDominio)
            .collect(Collectors.toList());
    }
}