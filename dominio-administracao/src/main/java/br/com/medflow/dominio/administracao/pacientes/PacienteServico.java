package br.com.medflow.dominio.administracao.pacientes;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

public class PacienteServico {
    private final PacienteRepositorio repositorio;
    
    public PacienteServico(PacienteRepositorio repositorio) {
        notNull(repositorio, "O repositório de pacientes não pode ser nulo");
        this.repositorio = repositorio;
    }
    
    public Paciente cadastrar(String nome, String cpf, String dataNascimento, String telefone, String endereco, UsuarioResponsavelId responsavelId) {
        
        var existente = repositorio.obterPorCpf(cpf);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("O CPF '" + cpf + "' já está cadastrado no sistema.");
        }
        
        var novo = new Paciente(nome, cpf, dataNascimento, telefone, endereco, responsavelId);
        repositorio.salvar(novo);
        return novo;
    }
    
    public Paciente atualizarDadosCadastrais(
            PacienteId id,
            String novoNome,
            String novoCpf,
            String novaDataNascimento,
            String novoTelefone,
            String novoEndereco,
            UsuarioResponsavelId responsavelId) {
        
        var paciente = obter(id);
        
        paciente.atualizarDados(novoNome, novoCpf, novaDataNascimento, novoTelefone, novoEndereco, responsavelId);
        repositorio.salvar(paciente);
        return paciente;
    }
    
    public void remover(PacienteId id, UsuarioResponsavelId responsavelId, boolean temProntuario, boolean temConsulta, boolean temExame) {
        var paciente = obter(id);
        
        paciente.validarRemocao(temProntuario, temConsulta, temExame);
        paciente.adicionarEntradaHistorico(AcaoHistorico.REMOCAO, "Paciente removido do sistema.", responsavelId);
        
        repositorio.remover(id);
    }
    
    public Paciente obter(PacienteId id) {
        return repositorio.obter(id);
    }
    
    public Optional<Paciente> pesquisarPorCpf(String cpf) {
        return repositorio.obterPorCpf(cpf);
    }
    
    public List<Paciente> pesquisarTodos() {
        return repositorio.pesquisar();
    }
}