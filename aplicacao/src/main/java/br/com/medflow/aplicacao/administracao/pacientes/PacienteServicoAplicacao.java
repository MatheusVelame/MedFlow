package br.com.medflow.aplicacao.administracao.pacientes;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.medflow.dominio.administracao.pacientes.Paciente;
import br.com.medflow.dominio.administracao.pacientes.PacienteId;
import br.com.medflow.dominio.administracao.pacientes.PacienteRepositorio;
import br.com.medflow.dominio.administracao.pacientes.PacienteServico;
import br.com.medflow.dominio.administracao.pacientes.UsuarioResponsavelId;

import com.medflow.dominio.prontuario.ProntuarioRepositorio;
import br.com.medflow.dominio.atendimento.consultas.ConsultaRepositorio;
import br.com.medflow.dominio.atendimento.exames.ExameRepositorio;

@Service
public class PacienteServicoAplicacao {
    
    private final PacienteServico servicoDominio;
    private final PacienteRepositorioAplicacao repositorioAplicacao;
    
    // Novas dependências para verificação de segurança
    private final ProntuarioRepositorio prontuarioRepo;
    private final ConsultaRepositorio consultaRepo;
    private final ExameRepositorio exameRepo;
    
    public PacienteServicoAplicacao(
            @Qualifier("pacienteRepositorioJpa") PacienteRepositorio repositorioDominio,
            PacienteRepositorioAplicacao repositorioAplicacao,
            // Injeção dos repositórios
            ProntuarioRepositorio prontuarioRepo,
            ConsultaRepositorio consultaRepo,
            ExameRepositorio exameRepo) {
        
        notNull(repositorioDominio, "O repositório de domínio não pode ser nulo");
        notNull(repositorioAplicacao, "O repositório de aplicação não pode ser nulo");
        // Validações básicas das novas dependências
        notNull(prontuarioRepo, "O repositório de prontuário não pode ser nulo");
        notNull(consultaRepo, "O repositório de consulta não pode ser nulo");
        notNull(exameRepo, "O repositório de exame não pode ser nulo");
        
        this.servicoDominio = new PacienteServico(repositorioDominio);
        this.repositorioAplicacao = repositorioAplicacao;
        
        this.prontuarioRepo = prontuarioRepo;
        this.consultaRepo = consultaRepo;
        this.exameRepo = exameRepo;
    }
    
    @Transactional
    public PacienteDetalhes cadastrarPaciente(
            String nome, 
            String cpf, 
            String dataNascimento, 
            String telefone, 
            String endereco, 
            int responsavelId) {
        
        UsuarioResponsavelId usuarioId = new UsuarioResponsavelId(responsavelId);
        
        Paciente paciente = servicoDominio.cadastrar(
            nome, cpf, dataNascimento, telefone, endereco, usuarioId
        );
        
        return repositorioAplicacao.buscarDetalhesPorId(paciente.getId().getId())
            .orElseThrow(() -> new IllegalStateException("Erro ao recuperar paciente cadastrado"));
    }
    
    @Transactional
    public PacienteDetalhes atualizarDadosCadastrais(
            int id,
            String novoNome,
            String novoCpf,
            String novaDataNascimento,
            String novoTelefone,
            String novoEndereco,
            int responsavelId) {
        
        PacienteId pacienteId = new PacienteId(id);
        UsuarioResponsavelId usuarioId = new UsuarioResponsavelId(responsavelId);
        
        Paciente paciente = servicoDominio.atualizarDadosCadastrais(
            pacienteId, novoNome, novoCpf, novaDataNascimento, 
            novoTelefone, novoEndereco, usuarioId
        );
        
        return repositorioAplicacao.buscarDetalhesPorId(paciente.getId().getId())
            .orElseThrow(() -> new IllegalStateException("Erro ao recuperar paciente atualizado"));
    }
    
    @Transactional
    public void removerPaciente(int id, int responsavelId) {
        // BLINDAGEM: Não aceitamos mais booleanos de fora. 
        // Nós mesmos verificamos no banco.
        
        PacienteId pacienteId = new PacienteId(id);
        UsuarioResponsavelId usuarioId = new UsuarioResponsavelId(responsavelId);
        
        // 1. Verifica vínculos reais no banco de dados
        // Nota: Seus repositórios precisam ter o método 'existsByPacienteId'
        boolean temProntuario = prontuarioRepo.existsByPacienteId(String.valueOf(id));
        boolean temConsulta = consultaRepo.existsByPacienteId(id);
        boolean temExame = exameRepo.existsByPacienteId((long) id);
        
        // 2. Chama o domínio passando a verdade
        servicoDominio.remover(pacienteId, usuarioId, temProntuario, temConsulta, temExame);
    }
    
    @Transactional(readOnly = true)
    public Optional<PacienteDetalhes> buscarPorId(int id) {
        return repositorioAplicacao.buscarDetalhesPorId(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<PacienteResumo> buscarResumoPorCpf(String cpf) {
        return repositorioAplicacao.buscarResumoPorCpf(cpf);
    }
    
    @Transactional(readOnly = true)
    public List<PacienteResumo> listarTodos() {
        return repositorioAplicacao.listarTodos();
    }
}