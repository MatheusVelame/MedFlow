package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import br.com.medflow.aplicacao.prontuario.HistoricoItemResponse;
import br.com.medflow.aplicacao.prontuario.ProntuarioDetalhes;
import br.com.medflow.aplicacao.prontuario.ProntuarioRepositorioAplicacao;
import br.com.medflow.aplicacao.prontuario.ProntuarioResumo;
import com.medflow.dominio.prontuario.Prontuario;
import com.medflow.dominio.prontuario.ProntuarioRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação final do repositório de Prontuário usando Decorator Pattern.
 * Compõe os decorators (logging) sobre a implementação base.
 */
@Component
public class ProntuarioRepositorioImpl implements ProntuarioRepositorio, ProntuarioRepositorioAplicacao {

    private final ProntuarioRepositorio repositorioDecorado;

    private final ProntuarioJpaRepository jpaRepository;

    public ProntuarioRepositorioImpl(ProntuarioRepositorioBase repositorioBase, ProntuarioJpaRepository jpaRepository) {
        this.repositorioDecorado = new ProntuarioRepositorioLoggingDecorator(repositorioBase);
        this.jpaRepository = jpaRepository;
    }

    // =====================================================================
    // IMPLEMENTAÇÃO DO DOMAIN REPOSITORY (PORTA DE ESCRITA/CUD)
    // =====================================================================

    @Override
    public void salvar(Prontuario prontuario) {
        repositorioDecorado.salvar(prontuario);
    }

    @Override
    public Optional<Prontuario> obterPorId(String id) {
        return repositorioDecorado.obterPorId(id);
    }

    @Override
    public List<Prontuario> buscarPorPaciente(String pacienteId) {
        return repositorioDecorado.buscarPorPaciente(pacienteId);
    }

    @Override
    public List<Prontuario> listarTodos() {
        return repositorioDecorado.listarTodos();
    }

    // =====================================================================
    // IMPLEMENTAÇÃO DO APPLICATION REPOSITORY (PORTA DE LEITURA/QUERY)
    // =====================================================================

    @Override
    public List<ProntuarioResumo> pesquisarResumos() {
        return listarTodos().stream()
                .map(this::toResumoDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProntuarioDetalhes> obterDetalhesPorId(String id) {
        return obterPorId(id)
                .map(this::toDetalhesDto);
    }

    @Override
    public List<HistoricoItemResponse> listarHistoricoClinico(String prontuarioId) {
        return obterPorId(prontuarioId)
                .map(prontuario -> prontuario.getHistoricoClinico().stream()
                        .map(this::toHistoricoItemResponse)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    public boolean existePorMedicoId(Integer medicoId) {
        if (medicoId == null) return false;
        // O campo profissionalResponsavel no JPA é String, então convertemos o ID
        return jpaRepository.existsByProfissionalResponsavel(String.valueOf(medicoId));
    }

    // Mapeamento Domain -> DTO
    private ProntuarioResumo toResumoDto(Prontuario prontuario) {
        return new ProntuarioResumo(
                prontuario.getId(),
                prontuario.getPacienteId(),
                prontuario.getAtendimentoId(),
                prontuario.getStatus(),
                prontuario.getDataHoraCriacao(),
                prontuario.getProfissionalResponsavel()
        );
    }

    private ProntuarioDetalhes toDetalhesDto(Prontuario prontuario) {
        List<ProntuarioDetalhes.HistoricoItemDetalhes> historicoClinico = prontuario.getHistoricoClinico().stream()
                .map(hc -> new ProntuarioDetalhes.HistoricoItemDetalhes(
                        hc.getId(),
                        hc.getSintomas(),
                        hc.getDiagnostico(),
                        hc.getConduta(),
                        hc.getDataHoraRegistro(),
                        hc.getProfissionalResponsavel(),
                        hc.getAnexosReferenciados()
                ))
                .collect(Collectors.toList());

        List<ProntuarioDetalhes.AtualizacaoDetalhes> historicoAtualizacoes = prontuario.getHistoricoAtualizacoes().stream()
                .map(ha -> new ProntuarioDetalhes.AtualizacaoDetalhes(
                        ha.getId(),
                        ha.getAtendimentoId(),
                        ha.getDataHoraAtualizacao(),
                        ha.getProfissionalResponsavel(),
                        ha.getObservacoes(),
                        ha.getStatus()
                ))
                .collect(Collectors.toList());

        return new ProntuarioDetalhes(
                prontuario.getId(),
                prontuario.getPacienteId(),
                prontuario.getAtendimentoId(),
                prontuario.getStatus(),
                prontuario.getDataHoraCriacao(),
                prontuario.getProfissionalResponsavel(),
                prontuario.getObservacoesIniciais(),
                historicoClinico,
                historicoAtualizacoes
        );
    }

    private HistoricoItemResponse toHistoricoItemResponse(com.medflow.dominio.prontuario.HistoricoClinico historico) {
        return new HistoricoItemResponse(
                historico.getId(),
                historico.getSintomas(),
                historico.getDiagnostico(),
                historico.getConduta(),
                historico.getDataHoraRegistro(),
                historico.getProfissionalResponsavel(),
                historico.getAnexosReferenciados()
        );
    }
}
