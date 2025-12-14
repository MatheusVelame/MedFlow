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

    // 🆕 Gestor de disponibilidades (interface da camada de aplicação)
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
        return medico != null ? strategy.converterParaDetalhes(medico) : null;
    }

    public MedicoDetalhes obterPorCrm(String crmCompleto) {
        notEmpty(crmCompleto, "O CRM não pode ser vazio");
        CRM crm = new CRM(crmCompleto);
        Medico medico = medicoRepositorioLeitura.obterPorCrm(crm).orElse(null);
        return medico != null ? strategy.converterParaDetalhes(medico) : null;
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

    /**
     * Cadastra novo médico.
     * VERSÃO COM SUPORTE A DISPONIBILIDADES.
     */
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

        // Monta CRM completo
        String crmCompleto = request.getCrmNumero() + "-" + request.getCrmUf();
        CRM crm = new CRM(crmCompleto);

        // Verifica se CRM já existe
        if (medicoRepositorioLeitura.obterPorCrm(crm).isPresent()) {
            throw new IllegalArgumentException("CRM já cadastrado: " + crmCompleto);
        }

        // 1. CONVERSÃO: Converte o Integer para o Value Object Medico.EspecialidadeId
        Medico.EspecialidadeId especialidadeId = new Medico.EspecialidadeId(request.getEspecialidadeId());

        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(1); // ID de exemplo do responsável

        // 2. Cria a entidade de domínio Medico (o histórico de criação é gerado pelo construtor/Funcionario)
        Medico novoMedico = new Medico(
                null, // FuncionarioId id (Nulo para que o JPA gere)
                request.getNome(),
                "Médico", // Função padrão
                request.getContato(),
                crm,
                especialidadeId,
                responsavelId
        );

        // 3. Salva o médico no repositório de escrita
        medicoRepositorioEscrita.salvar(novoMedico);

        System.out.println("Médico salvo no banco com sucesso.");

        // 🆕 4. Salvar disponibilidades se fornecidas
        if (request.getDisponibilidades() != null && !request.getDisponibilidades().isEmpty()) {
            // Busca o médico recém-salvo para pegar o ID gerado
            Medico medicoSalvo = medicoRepositorioLeitura.obterPorCrm(crm)
                    .orElseThrow(() -> new RuntimeException("Erro ao buscar médico recém-cadastrado"));

            // Converte para o formato da interface
            List<DisponibilidadeGestor.DisponibilidadeRequest> disponibilidades =
                    converterDisponibilidades(request.getDisponibilidades());

            disponibilidadeGestor.salvarDisponibilidades(medicoSalvo.getId(), disponibilidades);
            System.out.println("Disponibilidades salvas com sucesso.");
        }

        // 5. Busca o médico completo (com disponibilidades) e retorna o DTO
        return medicoRepositorioLeitura.obterPorCrm(crm)
                .map(strategy::converterParaDetalhes)
                .orElse(null);
    }

    /**
     * Atualiza médico existente.
     * Usa AcaoHistorico.ATUALIZACAO do domínio.
     */
    @Transactional
    public MedicoDetalhes atualizar(Integer id, MedicoAtualizacaoRequest request) {
        notNull(id, "O ID não pode ser nulo");
        notNull(request, "O request não pode ser nulo");

        // Busca médico existente
        FuncionarioId medicoId = new FuncionarioId(id.toString());
        Medico medicoExistente = medicoRepositorioLeitura.obterPorId(medicoId)
                .orElse(null);

        if (medicoExistente == null) {
            return null;
        }

        // Determina valores atualizados
        String novoNome = (request.getNome() != null && !request.getNome().isEmpty())
                ? request.getNome()
                : medicoExistente.getNome();

        String novoContato = (request.getContato() != null && !request.getContato().isEmpty())
                ? request.getContato()
                : medicoExistente.getContato();

        // Adiciona entrada de atualização no histórico
        List<Funcionario.HistoricoEntrada> historicoAtualizado = new ArrayList<>(medicoExistente.getHistorico());
        historicoAtualizado.add(new Funcionario.HistoricoEntrada(
                AcaoHistorico.ATUALIZACAO,
                "Dados atualizados",
                new UsuarioResponsavelId(1),
                LocalDateTime.now()
        ));

        // Recria médico com dados atualizados
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

        // Salva
        medicoRepositorioEscrita.salvar(medicoAtualizado);

        // 🆕 Atualiza disponibilidades se fornecidas
        if (request.getDisponibilidades() != null) {
            // Converte para o formato da interface
            List<DisponibilidadeGestor.DisponibilidadeRequest> disponibilidades =
                    converterDisponibilidadesAtualizacao(request.getDisponibilidades());

            disponibilidadeGestor.atualizarDisponibilidades(medicoId, disponibilidades);
        }

        // Retorna atualizado
        return medicoRepositorioLeitura.obterPorId(medicoId)
                .map(strategy::converterParaDetalhes)
                .orElse(null);
    }

    /**
     * Remove médico do sistema.
     * LÓGICA ROBUSTA:
     * 1. Verifica vínculos externos (Consultas, Exames, Prontuários).
     * 2. Se houver vínculo ou histórico relevante -> Inativa (Soft Delete).
     * 3. Se não houver vínculo e for um registro limpo -> Exclui permanentemente (Hard Delete).
     */
    @Transactional
    public boolean remover(Integer id) {
        notNull(id, "O ID não pode ser nulo");

        // 1. Busca médico
        FuncionarioId medicoId = new FuncionarioId(id.toString());
        Medico medicoExistente = medicoRepositorioLeitura.obterPorId(medicoId)
                .orElse(null);

        if (medicoExistente == null) {
            return false;
        }

        // 2. VERIFICAÇÃO DE VÍNCULOS
        boolean temConsultas = consultaRepositorio.existePorMedicoId(id);
        boolean temProntuarios = prontuarioRepositorio.existePorMedicoId(id);
        boolean temExames = exameRepositorio.existePorMedicoId(id);

        // Verifica se houve alterações cadastrais anteriores (histórico > 1 indica que não é apenas a criação)
        boolean temHistoricoInterno = medicoExistente.getHistorico().size() > 1;

        boolean possuiVinculosRelevantes = temConsultas || temProntuarios || temExames || temHistoricoInterno;

        if (possuiVinculosRelevantes) {
            // --- SOFT DELETE (Inativação) ---
            System.out.println("Médico possui vínculos. Realizando Inativação.");

            List<Funcionario.HistoricoEntrada> historicoAtualizado = new ArrayList<>(medicoExistente.getHistorico());

            // Monta string descrevendo o motivo
            String motivo = "Médico inativado. Vínculos detectados: " +
                    (temConsultas ? "[Consultas] " : "") +
                    (temProntuarios ? "[Prontuários] " : "") +
                    (temExames ? "[Exames] " : "") +
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
            // --- HARD DELETE (Exclusão Física) ---
            System.out.println("Médico sem vínculos. Realizando Exclusão Física.");
            medicoRepositorioEscrita.remover(medicoExistente.getId());
        }

        return true;
    }

    // ========== MÉTODOS AUXILIARES PRIVADOS ==========

    /**
     * Converte disponibilidades do request de cadastro para o formato da interface.
     */
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

    /**
     * Converte disponibilidades do request de atualização para o formato da interface.
     */
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