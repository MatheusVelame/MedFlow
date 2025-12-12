package br.com.medflow.dominio.referencia.especialidades;

/**
 * Proxy: Intercepta as chamadas para adicionar comportamento (ex: Log/Auditoria)
 * antes de repassar para a implementação real.
 */
public class EspecialidadeServicoProxy implements IEspecialidadeServico {

    private final IEspecialidadeServico servicoReal;

    public EspecialidadeServicoProxy(IEspecialidadeServico servicoReal) {
        this.servicoReal = servicoReal;
    }

    private void log(String operacao, String detalhe) {
        System.out.println("[AUDITORIA - PROXY] Operação: " + operacao + " | Detalhes: " + detalhe);
    }

    @Override
    public Especialidade cadastrar(String nome, String descricao) {
        log("CADASTRAR", "Tentativa de cadastro: " + nome);
        // Aqui você poderia checar permissões antes de chamar o real
        return servicoReal.cadastrar(nome, descricao);
    }

    @Override
    public Especialidade cadastrarComStatusProibido(String nome, String status) {
        log("CADASTRAR_STATUS", "Tentativa de cadastro com status manual: " + nome);
        return servicoReal.cadastrarComStatusProibido(nome, status);
    }

    @Override
    public Especialidade alterar(String nomeOriginal, String novoNome, String novaDescricao) {
        log("ALTERAR", "De: " + nomeOriginal + " Para: " + novoNome);
        return servicoReal.alterar(nomeOriginal, novoNome, novaDescricao);
    }

    @Override
    public Especialidade tentarAlterarComVinculo(String nomeOriginal, String novoNome) {
        log("TENTAR_ALTERAR_VINCULO", "Nome: " + nomeOriginal);
        return servicoReal.tentarAlterarComVinculo(nomeOriginal, novoNome);
    }

    @Override
    public Especialidade alterarStatus(String nome, String status) {
        log("ALTERAR_STATUS", "Nome: " + nome + " Novo Status: " + status);
        return servicoReal.alterarStatus(nome, status);
    }

    @Override
    public void excluir(String nome) {
        log("EXCLUIR", "Alvo: " + nome);
        servicoReal.excluir(nome);
    }

    @Override
    public void tentarInativarDuranteExclusao(String nome, String status) {
        log("TENTATIVA_INATIVACAO_EXCLUSAO", "Alvo: " + nome);
        servicoReal.tentarInativarDuranteExclusao(nome, status);
    }

    @Override
    public void atribuirMedico(String nomeMedico, String nomeEspecialidade) {
        log("ATRIBUIR_MEDICO", "Médico: " + nomeMedico + " -> Especialidade: " + nomeEspecialidade);
        servicoReal.atribuirMedico(nomeMedico, nomeEspecialidade);
    }
}