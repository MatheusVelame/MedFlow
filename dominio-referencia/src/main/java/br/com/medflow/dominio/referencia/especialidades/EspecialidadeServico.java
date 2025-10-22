package br.com.medflow.dominio.referencia.especialidades;

import java.util.Optional;

public class EspecialidadeServico {

    private final EspecialidadeRepositorio especialidadeRepositorio;
    private final MedicoRepositorio medicoRepositorio;

    public EspecialidadeServico(EspecialidadeRepositorio especialidadeRepositorio, MedicoRepositorio medicoRepositorio) {
        this.especialidadeRepositorio = especialidadeRepositorio;
        this.medicoRepositorio = medicoRepositorio;
    }

    public Especialidade cadastrar(String nome, String descricao) {
        if (especialidadeRepositorio.existePorNome(nome)) {
            throw new RegraNegocioException("Já existe uma especialidade com este nome");
        }

        Especialidade novaEspecialidade = new Especialidade(nome, descricao);
        especialidadeRepositorio.salvar(novaEspecialidade);
        return novaEspecialidade;
    }
    
    public Especialidade cadastrarComStatusProibido(String nome, String status) {
        return cadastrar(nome, "Descrição");
    }

    /*public Especialidade alterar(String nomeOriginal, String novoNome, String novaDescricao) {
        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nomeOriginal)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada."));

        if (!nomeOriginal.equals(novoNome) && medicoRepositorio.contarMedicosAtivosVinculados(nomeOriginal) > 0) {
            throw new RegraNegocioException("Não é possível alterar o nome: existem médicos ativos vinculados"); 
        }

        if (!nomeOriginal.equals(novoNome)) {
            if (especialidadeRepositorio.existePorNome(novoNome)) {
                throw new RegraNegocioException("Já existe outra especialidade com este nome");
            }
            especialidade.alterarNome(novoNome);
        }

        if (novaDescricao != null) {
            especialidade.alterarDescricao(novaDescricao);
        }

        especialidadeRepositorio.salvar(especialidade);
        return especialidade;
    } */
    public Especialidade alterar(String nomeOriginal, String novoNome, String novaDescricao) {
        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nomeOriginal)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada."));

        if (!nomeOriginal.equals(novoNome) && medicoRepositorio.contarMedicosAtivosVinculados(nomeOriginal) > 0) {
            throw new RegraNegocioException("Não é possível alterar o nome: existem médicos ativos vinculados"); 
        }

        if (!nomeOriginal.equals(novoNome)) {
            if (especialidadeRepositorio.existePorNome(novoNome)) {
                throw new RegraNegocioException("Já existe outra especialidade com este nome");
            }
            
            // --- FIX INÍCIO ---
            // Cria um objeto temporário com o NOME ORIGINAL para forçar a remoção no repositório.
            // Usa o construtor de carga de dados para criar uma 'cópia' de remoção.
            Especialidade entidadeAntiga = new Especialidade(
                nomeOriginal, 
                especialidade.getDescricao(), 
                especialidade.getStatus(), 
                especialidade.isPossuiVinculoHistorico()
            );
            especialidadeRepositorio.remover(entidadeAntiga);
            // --- FIX FIM ---

            especialidade.alterarNome(novoNome);
        }

        if (novaDescricao != null) {
            especialidade.alterarDescricao(novaDescricao);
        }

        especialidadeRepositorio.salvar(especialidade);
        return especialidade;
    }
    
    public Especialidade tentarAlterarComVinculo(String nomeOriginal, String novoNome) {
        return alterar(nomeOriginal, novoNome, null);
    }
    
    public Especialidade alterarStatus(String nome, String status) {
        throw new RegraNegocioException("Apenas o nome e a descrição podem ser alterados");
    }

    public void excluir(String nome) {
        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nome)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada para exclusão."));

        if (medicoRepositorio.contarMedicosAtivosVinculados(nome) > 0) {
            throw new RegraNegocioException("Não é possível excluir: existem médicos ativos vinculados");
        }
        
        if (especialidade.isPossuiVinculoHistorico()) {
            especialidade.inativar();
            especialidadeRepositorio.salvar(especialidade);
        } else {
            especialidadeRepositorio.remover(especialidade);
        }
    }
    
    public void tentarInativarDuranteExclusao(String nome, String status) {
        excluir(nome); 
    }

    public void atribuirMedico(String nomeMedico, String nomeEspecialidade) {
        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nomeEspecialidade)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada para atribuição."));

        if (especialidade.getStatus() == StatusEspecialidade.INATIVA) {
            throw new RegraNegocioException("Não é possível atribuir a médicos: a especialidade está inativa");
        }

        especialidade.registrarVinculoHistorico();
        especialidadeRepositorio.salvar(especialidade);
    }
}