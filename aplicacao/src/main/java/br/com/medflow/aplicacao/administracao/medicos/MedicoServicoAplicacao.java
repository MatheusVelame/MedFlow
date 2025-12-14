package br.com.medflow.aplicacao.administracao.medicos;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import br.com.medflow.aplicacao.atendimento.consultas.ConsultaRepositorioAplicacao;
import br.com.medflow.aplicacao.prontuario.ProntuarioRepositorioAplicacao;
import br.com.medflow.dominio.administracao.funcionarios.*;
import br.com.medflow.dominio.atendimento.exames.ExameRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para Médicos (Queries e Commands).
 */
@Service
public class MedicoServicoAplicacao {

    private final MedicoRepositorioAplicacao medicoRepositorioLeitura;
    private final FuncionarioRepositorio medicoRepositorioEscrita;
    private final MedicoConversaoStrategy strategy;

    // Dependências para verificação de integridade referencial
    private final ConsultaRepositorioAplicacao consultaRepositorio;
    private final ProntuarioRepositorioAplicacao prontuarioRepositorio;
    private final ExameRepositorio exameRepositorio;

    // Gestor de disponibilidades
    private final DisponibilidadeGestor disponibilidadeGestor;

    @Autowired
    public MedicoServicoAplicacao(
            @Qualifier("medicoRepositorioAplicacaoImpl") MedicoRepositorioAplicacao medicoRepositorioLeitura,
            @Qualifier("medicoRepositorioImpl") FuncionarioRepositorio medicoRepositorioEscrita,
            MedicoConversaoStrategy strategy,
            ConsultaRepositorioAplicacao consultaRepositorio,
            ProntuarioRepositorioAplicacao prontuarioRepositorio,
            ExameRepositorio exameRepositorio,
            DisponibilidadeGestor disponibilidadeGestor) {

        notNull(medicoRepositorioLeitura, "O repositório de leitura não pode ser nulo");
        notNull(medicoRepositorioEscrita, "O repositório de escrita não pode ser nulo");
        notNull(strategy, "A estratégia de conversão não pode ser nula");
        notNull(consultaRepositorio, "O repositório de consultas não pode ser nulo");
        notNull(prontuarioRepositorio, "O repositório de prontuários não pode ser nulo");
        notNull(exameRepositorio, "O repositório de exames não pode ser nulo");
        notNull(disponibilidadeGestor, "O gestor de disponibilidades não pode ser nulo");

        this.medicoRepositorioLeitura = medicoRepositorioLeitura;
        this.medicoRepositorioEscrita = medicoRepositorioEscrita;
        this.strategy = strategy;
        this.consultaRepositorio = consultaRepositorio;
        this.prontuarioRepositorio = prontuarioRepositorio;
        this.exameRepositorio = exameRepositorio;
        this.disponibilidadeGestor = disponibilidadeGestor;
    }

    // ========== QUERIES (LEITURA) ==========

    public List<MedicoResumo> listarTodos() {
        List<Medico> medicos = medicoRepositorioLeitura.pesquisarTodos();
        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    public MedicoDetalhes obterPorId(Integer id) {
        notNull(id, "O ID não pode ser nulo");
        FuncionarioId medicoId = new FuncionarioId(id.toString());
        Medico medico = medicoRepositorioLeitura.obterPorId(medicoId).orElse(null);

        if (medico == null) {
            return null;
        }

        // 🆕 Converte para detalhes E adiciona informações de vínculos
        MedicoDetalhes detalhes = strategy.converterParaDetalhes(medico);
        VinculosClinicosInfo vinculos = verificarVinculosClinicosDoMedico(id);

        // Cria novo MedicoDetalhes com vínculos
        return new MedicoDetalhes(
                detalhes.getId(),
                detalhes.getNome(),
                detalhes.getFuncao(),
                detalhes.getContato(),
                detalhes.getStatus(),
                detalhes.getHistorico(),
                detalhes.getCrm(),
                detalhes.getEspecialidade(),
                detalhes.getDataNascimento(),
                detalhes.getHorariosDisponiveis(),
                vinculos.temConsultas,    // 🆕
                vinculos.temProntuarios,  // 🆕
                vinculos.temExames        // 🆕
        );
    }

    public MedicoDetalhes obterPorCrm(String crmCompleto) {
        notEmpty(crmCompleto, "O CRM não pode ser vazio");
        CRM crm = new CRM(crmCompleto);
        Medico medico = medicoRepositorioLeitura.obterPorCrm(crm).orElse(null);

        if (medico == null) {
            return null;
        }

        // Obtém o ID e busca detalhes completos (com vínculos)
        Integer id = Integer.parseInt(medico.getId().getId());
        return obterPorId(id);
    }

    public List<MedicoResumo> listarPorStatus(StatusFuncionario status) {
        notNull(status, "O status não pode ser nulo");
        List<Medico> medicos = medicoRepositorioLeitura.pesquisarPorStatus(status);
        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    public List<MedicoResumo> listarPorEspecialidade(Integer especialidadeId) {
        notNull(especialidadeId, "O ID da especialidade não pode ser nulo");
        List<Medico> medicos = medicoRepositorioLeitura.pesquisarPorEspecialidade(especialidadeId);
        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    public List<MedicoResumo> buscarGeral(String termoBusca) {
        notEmpty(termoBusca, "O termo de busca não pode ser vazio");
        List<Medico> medicos = medicoRepositorioLeitura.buscarGeral(termoBusca);
        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    // ========== COMMANDS (ESCRITA) ==========

    @Transactional
    public MedicoDetalhes cadastrar(MedicoCadastroRequest request) {
        notNull(request, "O request não pode ser nulo");
        notEmpty(request.getNome(), "O nome é obrigatório");
        notEmpty(request.getContato(), "O contato é obrigatório");
        notEmpty(request.getCrmNumero(), "O número do CRM é obrigatório");
        notEmpty(request.getCrmUf(), "A UF do CRM é obrigatória");
        notNull(request.getEspecialidadeId(), "A especialidade é obrigatória");

        System.out.println("=== CADASTRAR MÉDICO ===");
        System.out.println("Nome: " + request.getNome());
        System.out.println("CRM: " + request.getCrmNumero() + "-" + request.getCrmUf());

        String crmCompleto = request.getCrmNumero() + "-" + request.getCrmUf();
        CRM crm = new CRM(crmCompleto);

        if (medicoRepositorioLeitura.obterPorCrm(crm).isPresent()) {
            throw new IllegalArgumentException("CRM já cadastrado: " + crmCompleto);
        }

        Medico.EspecialidadeId especialidadeId = new Medico.EspecialidadeId(request.getEspecialidadeId());
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(1);

        Medico novoMedico = new Medico(
                null,
                request.getNome(),
                "Médico",
                request.getContato(),
                crm,
                especialidadeId,
                responsavelId
        );

        medicoRepositorioEscrita.salvar(novoMedico);
        System.out.println("Médico salvo no banco com sucesso.");

        if (request.getDisponibilidades() != null && !request.getDisponibilidades().isEmpty()) {
            Medico medicoSalvo = medicoRepositorioLeitura.obterPorCrm(crm)
                    .orElseThrow(() -> new RuntimeException("Erro ao buscar médico recém-cadastrado"));

            List<DisponibilidadeGestor.DisponibilidadeRequest> disponibilidades =
                    converterDisponibilidades(request.getDisponibilidades());

            disponibilidadeGestor.salvarDisponibilidades(medicoSalvo.getId(), disponibilidades);
            System.out.println("Disponibilidades salvas com sucesso.");
        }

        return medicoRepositorioLeitura.obterPorCrm(crm)
                .map(m -> obterPorId(Integer.parseInt(m.getId().getId())))
                .orElse(null);
    }

    @Transactional
    public MedicoDetalhes atualizar(Integer id, MedicoAtualizacaoRequest request) {
        notNull(id, "O ID não pode ser nulo");
        notNull(request, "O request não pode ser nulo");

        FuncionarioId medicoId = new FuncionarioId(id.toString());
        Medico medicoExistente = medicoRepositorioLeitura.obterPorId(medicoId)
                .orElse(null);

        if (medicoExistente == null) {
            return null;
        }

        String novoNome = (request.getNome() != null && !request.getNome().isEmpty())
                ? request.getNome()
                : medicoExistente.getNome();

        String novoContato = (request.getContato() != null && !request.getContato().isEmpty())
                ? request.getContato()
                : medicoExistente.getContato();

        List<Funcionario.HistoricoEntrada> historicoAtualizado = new ArrayList<>(medicoExistente.getHistorico());
        historicoAtualizado.add(new Funcionario.HistoricoEntrada(
                AcaoHistorico.ATUALIZACAO,
                "Dados atualizados",
                new UsuarioResponsavelId(1),
                LocalDateTime.now()
        ));

        Medico medicoAtualizado = new Medico(
                medicoExistente.getId(),
                novoNome,
                medicoExistente.getFuncao(),
                novoContato,
                medicoExistente.getStatus().name(),
                historicoAtualizado,
                medicoExistente.getCrm(),
                medicoExistente.getEspecialidade()
        );

        medicoRepositorioEscrita.salvar(medicoAtualizado);

        if (request.getDisponibilidades() != null) {
            List<DisponibilidadeGestor.DisponibilidadeRequest> disponibilidades =
                    converterDisponibilidadesAtualizacao(request.getDisponibilidades());

            disponibilidadeGestor.atualizarDisponibilidades(medicoId, disponibilidades);
        }

        return medicoRepositorioLeitura.obterPorId(medicoId)
                .map(m -> obterPorId(Integer.parseInt(m.getId().getId())))
                .orElse(null);
    }

    @Transactional
    public boolean remover(Integer id) {
        notNull(id, "O ID não pode ser nulo");

        FuncionarioId medicoId = new FuncionarioId(id.toString());
        Medico medicoExistente = medicoRepositorioLeitura.obterPorId(medicoId)
                .orElse(null);

        if (medicoExistente == null) {
            return false;
        }

        // 🆕 USA O MESMO MÉTODO DE VERIFICAÇÃO
        VinculosClinicosInfo vinculos = verificarVinculosClinicosDoMedico(id);

        boolean temHistoricoInterno = medicoExistente.getHistorico().size() > 1;
        boolean possuiVinculosRelevantes = vinculos.temConsultas || vinculos.temProntuarios ||
                vinculos.temExames || temHistoricoInterno;

        if (possuiVinculosRelevantes) {
            // SOFT DELETE
            System.out.println("Médico possui vínculos. Realizando Inativação.");

            List<Funcionario.HistoricoEntrada> historicoAtualizado = new ArrayList<>(medicoExistente.getHistorico());

            String motivo = "Médico inativado. Vínculos detectados: " +
                    (vinculos.temConsultas ? "[Consultas] " : "") +
                    (vinculos.temProntuarios ? "[Prontuários] " : "") +
                    (vinculos.temExames ? "[Exames] " : "") +
                    (temHistoricoInterno ? "[Histórico Cadastral]" : "");

            historicoAtualizado.add(new Funcionario.HistoricoEntrada(
                    AcaoHistorico.EXCLUSAO,
                    motivo.trim(),
                    new UsuarioResponsavelId(1),
                    LocalDateTime.now()
            ));

            Medico medicoInativo = new Medico(
                    medicoExistente.getId(),
                    medicoExistente.getNome(),
                    medicoExistente.getFuncao(),
                    medicoExistente.getContato(),
                    "INATIVO",
                    historicoAtualizado,
                    medicoExistente.getCrm(),
                    medicoExistente.getEspecialidade()
            );

            medicoRepositorioEscrita.salvar(medicoInativo);
        } else {
            // HARD DELETE
            System.out.println("Médico sem vínculos. Realizando Exclusão Física.");
            medicoRepositorioEscrita.remover(medicoExistente.getId());
        }

        return true;
    }

    // ========== MÉTODOS AUXILIARES PRIVADOS ==========

    /**
     * 🆕 MÉTODO REUTILIZÁVEL: Verifica vínculos clínicos do médico.
     * Usado tanto para detalhes quanto para decisão de exclusão.
     */
    private VinculosClinicosInfo verificarVinculosClinicosDoMedico(Integer medicoId) {
        boolean temConsultas = consultaRepositorio.existePorMedicoId(medicoId);
        boolean temProntuarios = prontuarioRepositorio.existePorMedicoId(medicoId);
        boolean temExames = exameRepositorio.existePorMedicoId(medicoId);

        return new VinculosClinicosInfo(temConsultas, temProntuarios, temExames);
    }

    /**
     * Record para encapsular informações de vínculos clínicos.
     */
    private record VinculosClinicosInfo(
            boolean temConsultas,
            boolean temProntuarios,
            boolean temExames
    ) {}

    private List<DisponibilidadeGestor.DisponibilidadeRequest> converterDisponibilidades(
            List<MedicoCadastroRequest.DisponibilidadeRequest> disponibilidades) {

        return disponibilidades.stream()
                .map(d -> new DisponibilidadeGestor.DisponibilidadeRequest(
                        d.getDiaSemana(),
                        d.getHoraInicio(),
                        d.getHoraFim()
                ))
                .collect(Collectors.toList());
    }

    private List<DisponibilidadeGestor.DisponibilidadeRequest> converterDisponibilidadesAtualizacao(
            List<MedicoAtualizacaoRequest.DisponibilidadeRequest> disponibilidades) {

        return disponibilidades.stream()
                .map(d -> new DisponibilidadeGestor.DisponibilidadeRequest(
                        d.getDiaSemana(),
                        d.getHoraInicio(),
                        d.getHoraFim()
                ))
                .collect(Collectors.toList());
    }
}